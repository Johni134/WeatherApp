package ru.geekbrains.evgeniy.weatherapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.model.ForecastModel;
import ru.geekbrains.evgeniy.weatherapp.model.ForecastModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.home.adapters.ForecastAdapter;

public class CityForecastActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewForecast;
    private RecyclerView recyclerViewForecast;

    private Long cityId;
    private String cityTitle;

    public static final String EXTRA_CITY_ID = "EXTRA_CITY_ID";
    public static final String EXTRA_CITY_TITLE = "EXTRA_CITY_TITLE";
    private static final String CONST_PARCELABLE_LIST = "const_parcelable_list";

    private ForecastAdapter forecastAdapter;

    private List<ForecastModel> forecastModelList;

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout
        setContentView(R.layout.forecast_city_weather);
        // init views
        initViews();

        Intent intent = getIntent();
        if(intent != null) {
            cityId = intent.getLongExtra(EXTRA_CITY_ID, 0);
            cityTitle = intent.getStringExtra(EXTRA_CITY_TITLE);
        }

        textViewTitle.setText(cityTitle);
        textViewForecast.setText(getString(R.string.forecast_for_5_days));

        if (savedInstanceState != null)
        {
            forecastModelList = savedInstanceState.getParcelableArrayList(CONST_PARCELABLE_LIST);
            if(forecastModelList != null) {
                forecastAdapter = new ForecastAdapter(forecastModelList);
                recyclerViewForecast.setAdapter(forecastAdapter);
            }
        }
        else {
            new Thread() {
                public void run() {
                    final ForecastModelArray forecastModelArray = WeatherDataLoader.getForecastWeatherByID(cityId.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (forecastModelArray == null || forecastModelArray.list.size() == 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
                            } else {
                                forecastModelList = forecastModelArray.list;
                                forecastAdapter = new ForecastAdapter(forecastModelList);
                                recyclerViewForecast.setAdapter(forecastAdapter);
                            }
                        }
                    });
                }
            }.start();
        }
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.tvTitle);
        textViewForecast = findViewById(R.id.tvForecast);
        recyclerViewForecast = findViewById(R.id.rvForecast);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewForecast.setLayoutManager(linearLayoutManager);
        recyclerViewForecast.setHasFixedSize(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CONST_PARCELABLE_LIST, (ArrayList<? extends Parcelable>) forecastModelList);
        super.onSaveInstanceState(outState);
    }
}
