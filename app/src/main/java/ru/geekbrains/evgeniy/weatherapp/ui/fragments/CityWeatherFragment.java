package ru.geekbrains.evgeniy.weatherapp.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;


public class CityWeatherFragment extends Fragment implements View.OnClickListener {

    private TextView cityTextView;
    private TextView updatedTextView;
    private TextView detailsTextView;
    private TextView currentTemperatureTextView;
    private TextView weatherIcon;
    private Button buttonShowHistory;

    private final Handler handler = new Handler();

    private final String EXTRA_CITY_MODEL_KEY = "city_model_key";

    private CityModel currentCityModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_weather, container, false);

        initViews(view);

        if (savedInstanceState != null) {
            currentCityModel = savedInstanceState.getParcelable(EXTRA_CITY_MODEL_KEY);
        }

        renderWeather(currentCityModel);

        return view;
    }

    public void setCityModel(CityModel cityModel) {
        currentCityModel = cityModel;
    }

    public CityModel getCityModel() {
        return currentCityModel;
    }

    private void initViews(View view) {
        cityTextView = view.findViewById(R.id.city_field);
        updatedTextView = view.findViewById(R.id.updated_field);
        detailsTextView = view.findViewById(R.id.details_field);
        currentTemperatureTextView = view.findViewById(R.id.current_temperature_field);
        weatherIcon = view.findViewById(R.id.weather_icon);
        buttonShowHistory = view.findViewById(R.id.buttonShowHistory);
        buttonShowHistory.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.city_weather_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Ловим нажатие кнопки меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_info) {
            updateWeatherData();
            return true;
        }
        return false;
    }

    //Обновление/загрузка погодных данных
    private void updateWeatherData() {
        new Thread() {//Отдельный поток для получения новых данных в фоне

            public void run() {
                final CityModel model = WeatherDataLoader.getWeatherByID(getActivity(), currentCityModel.id.toString());
                // Вызов методов напрямую может вызвать runtime error
                // Мы не можем напрямую обновить UI, поэтому используем handler, чтобы обновить интерфейс в главном потоке.
                if (model == null) {
                    handler.post(new Runnable() {

                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {

                        public void run() {
                            renderWeather(model);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(CityModel model) {
        try {
            cityTextView.setText(model.getName().toUpperCase(Locale.US) + ", " + model.sys.country);

            String description = "";
            long id = 0;

            if (model.weather.size() != 0) {
                description = model.weather.get(0).description.toUpperCase(Locale.US);
                id = model.weather.get(0).id;
            }
            detailsTextView.setText(description + "\n" + "Humidity: "
                                    + model.main.humidity + "%" + "\n" + "Pressure: " + model.main.pressure + " hPa");

            currentTemperatureTextView.setText(model.getTempC());

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(model.dt * 1000));
            updatedTextView.setText("Last update: " + updatedOn);

            setWeatherIcon(id, model.sys.sunrise * 1000,
                    model.sys.sunset * 1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWeatherIcon(long actualId, long sunrise, long sunset) {
        long id = actualId / 100; // Упрощение кодов (int оставляет только целочисленное значение)
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            }
            else {
                icon = getString(R.string.weather_clear_night);
            }
        }
        else {
            switch ((int) id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    break;
                // Можете доработать приложение, найдя все иконки и распарсив все значения
                default:
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_CITY_MODEL_KEY, currentCityModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShowHistory:
                Context activity = getActivity();
                if (activity instanceof CityWeatherListener)
                    ((CityWeatherListener) activity).showForecast(currentCityModel);
                break;
        }
    }
}
