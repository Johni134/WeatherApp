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
import ru.geekbrains.evgeniy.weatherapp.model.HistoryModel;
import ru.geekbrains.evgeniy.weatherapp.model.HistoryModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.home.adapters.HistoryAdapter;

public class CityHistoryActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewHistory;
    private RecyclerView recyclerViewHistory;

    private Long cityId;
    private String cityTitle;

    public static final String EXTRA_CITY_ID = "EXTRA_CITY_ID";
    public static final String EXTRA_CITY_TITLE = "EXTRA_CITY_TITLE";
    private static final String CONST_PARCELABLE_LIST = "const_parcelable_list";

    private HistoryAdapter historyAdapter;

    private List<HistoryModel> historyModelList;

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout
        setContentView(R.layout.history_city_weather);
        // init views
        initViews();

        Intent intent = getIntent();
        if(intent != null) {
            cityId = intent.getLongExtra(EXTRA_CITY_ID, 0);
            cityTitle = intent.getStringExtra(EXTRA_CITY_TITLE);
        }

        textViewTitle.setText(cityTitle);
        textViewHistory.setText(getString(R.string.history_for_5_days));

        if (savedInstanceState != null)
        {
            historyModelList = savedInstanceState.getParcelableArrayList(CONST_PARCELABLE_LIST);
            if(historyModelList != null) {
                historyAdapter = new HistoryAdapter(historyModelList);
                recyclerViewHistory.setAdapter(historyAdapter);
            }
        }
        else {
            new Thread() {
                public void run() {
                    final HistoryModelArray historyModelArray = WeatherDataLoader.getHistoryWeatherByID(cityId.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (historyModelArray == null || historyModelArray.list.size() == 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
                            } else {
                                historyModelList = historyModelArray.list;
                                historyAdapter = new HistoryAdapter(historyModelList);
                                recyclerViewHistory.setAdapter(historyAdapter);
                            }
                        }
                    });
                }
            }.start();
        }
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.tvTitle);
        textViewHistory = findViewById(R.id.tvHistory);
        recyclerViewHistory = findViewById(R.id.rvHistory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);
        recyclerViewHistory.setHasFixedSize(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CONST_PARCELABLE_LIST, (ArrayList<? extends Parcelable>) historyModelList);
        super.onSaveInstanceState(outState);
    }
}
