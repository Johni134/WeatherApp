package ru.geekbrains.evgeniy.weatherapp.ui.home;

import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.model.CoordModel;
import ru.geekbrains.evgeniy.weatherapp.model.PollutionModel;
import ru.geekbrains.evgeniy.weatherapp.model.PollutionModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.home.adapters.AirPollutionAdapter;

import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_CITY_ID;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_CITY_TITLE;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_LAT;
import static ru.geekbrains.evgeniy.weatherapp.data.SupportingLib.EXTRA_LON;

public class AirPollutionActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {

    private TabHost tabHost;
    private TabHost.TabSpec tabSpec;
    private final String TAB_CO = "tabCO";
    private final String TAB_O3 = "tabO3";
    private final String TAB_SO2 = "tabSO2";
    private final String TAB_NO2 = "tabNO2";

    private Long cityId;
    private String cityTitle;

    private TextView textViewTitle;
    private TextView textViewDescription;
    private RecyclerView rvTabCO;
    private TextView tvTabO3;
    private RecyclerView rvTabSO2;

    private CoordModel coordModel;

    private AirPollutionAdapter airPollutionAdapterAdapterCO;
    private AirPollutionAdapter airPollutionAdapterAdapterSO2;

    private List<PollutionModel> pollutionModelListCO;
    private List<PollutionModel> pollutionModelListSO2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.air_pollution);

        Intent intent = getIntent();
        if(intent != null) {
            cityId = intent.getLongExtra(EXTRA_CITY_ID, 0);
            cityTitle = intent.getStringExtra(EXTRA_CITY_TITLE);
            float lat = intent.getFloatExtra(EXTRA_LAT, 0);
            float lon = intent.getFloatExtra(EXTRA_LON, 0);
            coordModel = new CoordModel(lon, lat);
        }

        initViews();

        textViewTitle.setText(cityTitle);

    }

    private void initViews() {
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        addTabToTabHost(TAB_CO, getString(R.string.tabCO), R.id.tabCO);
        addTabToTabHost(TAB_O3, getString(R.string.tabO3), R.id.tabO3);
        addTabToTabHost(TAB_SO2, getString(R.string.tabSO2), R.id.tabSO2);
        addTabToTabHost(TAB_NO2, getString(R.string.tabNO2), R.id.tabNO2);

        tabHost.setOnTabChangedListener(this);

        tabHost.setCurrentTabByTag(TAB_CO);
        onTabChanged(TAB_CO);

        textViewTitle = findViewById(R.id.tvPollutionTitle);
        textViewDescription = findViewById(R.id.tvPollutionDescription);

        LinearLayoutManager linearLayoutManagerCO = new LinearLayoutManager(this);
        linearLayoutManagerCO.setOrientation(LinearLayoutManager.VERTICAL);
        rvTabCO = findViewById(R.id.tabCO);
        rvTabCO.setLayoutManager(linearLayoutManagerCO);

        LinearLayoutManager linearLayoutManagerSO2 = new LinearLayoutManager(this);
        linearLayoutManagerSO2.setOrientation(LinearLayoutManager.VERTICAL);
        rvTabSO2 = findViewById(R.id.tabSO2);
        rvTabSO2.setLayoutManager(linearLayoutManagerSO2);

        tvTabO3 = findViewById(R.id.tabO3);

    }

    private void addTabToTabHost(String tagName, String title, int content) {
        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(tagName);
        // название вкладки
        tabSpec.setIndicator(title);

        tabSpec.setContent(content);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(final String tabId) {
        String type = "";
        switch (tabId) {
            case TAB_CO:
                type = "co";
                break;
            case TAB_SO2:
                type = "so2";
                break;
        }
        if (type != "") {
            final String finalType = type;
            new Thread() {
                public void run() {
                    final PollutionModelArray pollutionModelArray = WeatherDataLoader.getPollutionByCoordsAndType(finalType, coordModel, getString(R.string.open_weather_maps_app_id));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (pollutionModelArray == null || pollutionModelArray.dataList.size() == 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
                            } else {
                                if (tabId == TAB_CO) {
                                    pollutionModelListCO = pollutionModelArray.dataList;
                                    airPollutionAdapterAdapterCO = new AirPollutionAdapter(pollutionModelListCO);
                                    rvTabCO.setAdapter(airPollutionAdapterAdapterCO);
                                } else {
                                    pollutionModelListSO2 = pollutionModelArray.dataList;
                                    airPollutionAdapterAdapterSO2 = new AirPollutionAdapter(pollutionModelListSO2);
                                    rvTabSO2.setAdapter(airPollutionAdapterAdapterSO2);
                                }
                            }
                        }
                    });
                }
            }.start();
        }
    }
}
