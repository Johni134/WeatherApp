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
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public class WidgetCitiesFactory  implements RemoteViewsService.RemoteViewsFactory, RealmChangeListener<RealmResults<CityModel>> {

    private Realm realm;
    private RealmResults<CityModel> dataRecords;
    private List<CityModel> records;
    private Context context;
    private final Handler handler = new Handler();

    public WidgetCitiesFactory(Context context, Intent intent) {
        this.context = context;
        realm = Realm.getInstance(WeatherApplication.getRealmConf());
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
        RemoteViews rView = new RemoteViews(context.getPackageName(),
                R.layout.item_widget);
        rView.setTextViewText(R.id.tvWidgetTitle, records.get(position).getNameWithCountry());
        rView.setTextViewText(R.id.tvWidgetTemp, records.get(position).getTempC());
        Intent clickIntent = new Intent();
        clickIntent.putExtra(WidgetCities.CITY_DESCRIPTION, records.get(position).getNameWithCountry() + ": " + records.get(position).getTempC());
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
        if (dataRecords.size() > 10) {
            records = realm.copyFromRealm(dataRecords.subList(0, 10));
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
