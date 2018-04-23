package ru.geekbrains.evgeniy.weatherapp.data;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;
import ru.geekbrains.evgeniy.weatherapp.model.HistoryModelArray;


/**
 * Вспомогательный класс для работы с API openweathermap.org и скачивания нужных
 * данных
 */

public class WeatherDataLoader {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/%s?%s=%s&units=metric";
    private static final String OPEN_WEATHER_MAP_API_OKHTTP = "http://api.openweathermap.org/data/2.5/%s";
    private static final String KEY = "x-api-key";
    private static final String APIKEY = "APPID";
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;
    private static final int ALL_GROUP_GOOD = 0;

    // переношу сюда app id, чтобы не использовать context
    private static final String OPEN_WEATHER_MAPS_APP_ID = "5a7b4adfb331abce78c619ae441ab686";

    enum MainParametr {
        WEATHER("weather"),
        GROUP("group"),
        FORECAST("forecast");

        private String description;

        private MainParametr(String description) {
            this.description = description;
        }

        public String getDescription() {return description;}
    };

    private static String getJSONWeatherByParametr(String query, String parametr) {
        return getJSONWeatherByParametr(query, parametr, MainParametr.WEATHER.getDescription());
    }

    private static String getJSONWeatherByParametr(String query, String parametr, String mainParametr) {
        // init
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(String.format(OPEN_WEATHER_MAP_API_OKHTTP, mainParametr)).newBuilder();
        urlBuilder.addQueryParameter(parametr, query);
        urlBuilder.addQueryParameter(APIKEY, OPEN_WEATHER_MAPS_APP_ID);
        urlBuilder.addQueryParameter("units", "metric");
        String url = urlBuilder.build().toString();

        // get request
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        /*
        try {
            //Используем API (Application programming interface) openweathermap
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, (isGroup ? "group" : "weather"), parametr, query));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty(KEY, context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);
            String tempVariable;
            while ((tempVariable = reader.readLine()) != null) {
                rawData.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            return rawData.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        */
    }

    public static CityModel getWeatherByCity(String city) {

        String jsonString = getJSONWeatherByParametr(city, "q");
        if (jsonString == null)
            return null;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CityModel model = gson.fromJson(jsonString, CityModel.class);

        if (model.cod != ALL_GOOD) {
            return null;
        }
        return model;
    }

    public static CityModel getWeatherByID(Context context, String id) {
        String jsonString = getJSONWeatherByParametr(id, "id");
        if (jsonString == null)
            return null;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CityModel model = gson.fromJson(jsonString, CityModel.class);

        if (model.cod != ALL_GOOD) {
            return null;
        }
        return model;
    }

    public static CityModelArray getListWeatherByIDs(String ids) {
        String jsonString = getJSONWeatherByParametr(ids, "id", MainParametr.GROUP.getDescription());
        if (jsonString == null)
            return null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CityModelArray cityModelArray = gson.fromJson(jsonString, CityModelArray.class);

        // тут если все хорошо прошло кода нет, а если плохо, то он есть,
        // поэтому с нулем будем сравнивать
        if (cityModelArray.cod != ALL_GROUP_GOOD) {
            return null;
        }
        return cityModelArray;
    }

    public static HistoryModelArray getHistoryWeatherByID(String id) {
        String jsonString = getJSONWeatherByParametr(id, "id", MainParametr.FORECAST.getDescription());
        if (jsonString == null)
            return null;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        HistoryModelArray historyModelArray = gson.fromJson(jsonString, HistoryModelArray.class);

        if (historyModelArray.cod != ALL_GOOD) {
            return null;
        }
        return historyModelArray;
    }
}
