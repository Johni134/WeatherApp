package ru.geekbrains.evgeniy.weatherapp;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;


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

    private static String getJSONWeatherByParametr(Context context, String query, String parametr) {
        return getJSONWeatherByParametr(context, query, parametr, false);
    }

    private static String getJSONWeatherByParametr(Context context, String query, String parametr, boolean isGroup) {
        // init
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(String.format(OPEN_WEATHER_MAP_API_OKHTTP, (isGroup ? "group" : "weather"))).newBuilder();
        urlBuilder.addQueryParameter(parametr, query);
        urlBuilder.addQueryParameter(APIKEY, context.getString(R.string.open_weather_maps_app_id));
        urlBuilder.addQueryParameter("units", "metric");
        String url = urlBuilder.build().toString();

        // get request
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
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

    public static CityModel getWeatherByCity(Context context, String city) {

        String jsonString = getJSONWeatherByParametr(context, city, "q");
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
        String jsonString = getJSONWeatherByParametr(context, id, "id");
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

    public static CityModelArray getListWeatherByIDs(Context context, String ids) {
        String jsonString = getJSONWeatherByParametr(context, ids, "id", true);
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

}
