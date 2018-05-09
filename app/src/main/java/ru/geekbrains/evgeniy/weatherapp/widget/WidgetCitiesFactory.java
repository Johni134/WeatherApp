package ru.geekbrains.evgeniy.weatherapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.WeatherApplication;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.SupportingLib;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public class WidgetCitiesFactory  implements RemoteViewsService.RemoteViewsFactory, RealmChangeListener<RealmResults<CityModel>> {

    private Realm realm;
    private RealmResults<CityModel> dataRecords;
    private List<CityModel> records;
    private Context context;
    private final Handler handler = new Handler();
    private int cities_num = 1;

    public WidgetCitiesFactory(Context context, Intent intent) {
        this.context = context;
        realm = Realm.getInstance(WeatherApplication.getRealmConf());
        // init shared pref
        WorkWithSharedPreferences.initSharedPreferences(context);

        cities_num = WorkWithSharedPreferences.getProperty(WidgetConfigureActivity.PREFS_CITIES_NUM, 1);
    }

    @Override
    public void onCreate() {
        records = new ArrayList<>();
        handler.post(new Runnable() {
            @Override
            public void run() {
                dataRecords = DataHelper.getDataForWidget(realm);
                dataRecords.addChangeListener(WidgetCitiesFactory.this);
                updateRecords();
                onMessage();
            }
        });
    }

    @Override
    public void onDataSetChanged() {
        /*
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateRecords();
            }
        });
        */
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView;
        CityModel cm = records.get(position);

        if (cm == null) return null;

        if (getCount() == 1) {
            rView = new RemoteViews(context.getPackageName(),
                    R.layout.item_widget_one_city);
            if (cm.main != null) {
                rView.setTextViewText(R.id.tvWidgetHumidity, context.getString(R.string.humidity) + ": " + cm.main.humidity + "%");
                rView.setTextViewText(R.id.tvWidgetPressure, context.getString(R.string.pressure) + ": " + cm.main.pressure + " hPa");
            }
            if (cm.sys != null) {
                rView.setTextViewText(R.id.tvWidgetSunrise, context.getString(R.string.sunrise) + ": " + SupportingLib.getTime(cm.sys.sunrise));
                rView.setTextViewText(R.id.tvWidgetSunset, context.getString(R.string.sunset) + ": " + SupportingLib.getTime(cm.sys.sunset));
            }
            if (cm.weather.size() > 0) {
                String description = cm.weather.first().description;
                if (description.length() > 2) {
                    description = Character.toUpperCase(description.charAt(0)) + description.substring(1);
                }
                rView.setTextViewText(R.id.tvWidgetDescription, description);
            }
            rView.setTextViewText(R.id.tvWidgetUpdate, SupportingLib.getLastUpdate(cm.dt));
        } else {
            rView = new RemoteViews(context.getPackageName(),
                    R.layout.item_widget);
        }
        rView.setTextViewText(R.id.tvWidgetTitle, cm.getNameWithCountry());
        rView.setTextViewText(R.id.tvWidgetTemp, cm.getTempC());
        Intent clickIntent = new Intent();
        clickIntent.putExtra(WidgetCities.CITY_DESCRIPTION, cm.getNameWithCountry() + ": " + cm.getTempC());
        rView.setOnClickFillInIntent(R.id.text, clickIntent);

        return  rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onChange(RealmResults<CityModel> cityModels) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateRecords();
                onMessage();
            }
        });
    }

    private void updateRecords() {
        if (dataRecords.size() > cities_num) {
            records = realm.copyFromRealm(dataRecords.subList(0, cities_num));
        }
        else {
            records = realm.copyFromRealm(dataRecords.subList(0, dataRecords.size()));
        }
    }

    protected void onMessage() {
        Intent intent_meeting_update = new  Intent(context, WidgetCities.class);
        intent_meeting_update.setAction(WidgetCities.UPDATE_WIDGET_ACTION);
        context.sendBroadcast(intent_meeting_update);
    }
}
