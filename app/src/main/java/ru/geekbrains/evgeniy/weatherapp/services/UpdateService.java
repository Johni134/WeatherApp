package ru.geekbrains.evgeniy.weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.WeatherApplication;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.fragments.SettingsFragment;

public class UpdateService extends Service {

    private UpdateWeathersBinder binder = new UpdateWeathersBinder();
    Timer timer;
    TimerTask tTask;
    private long interval = 300000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class UpdateWeathersBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        schedule();
    }

    void schedule() {
        if (tTask != null) tTask.cancel();
        if (interval > 0) {
            tTask = new TimerTask() {
                public void run() {
                    if (WorkWithSharedPreferences.getProperty(SettingsFragment.PREF_UPDATE_EVERY_5_MINUTES, false))
                        updateWeathers();
                }
            };
            timer.schedule(tTask, 1000, interval);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        tTask.cancel();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public void updateWeathers() {
        Realm realm = Realm.getInstance(WeatherApplication.getRealmConf());
        DataHelper.updateAllWeathers(realm, getString(R.string.open_weather_maps_app_id));
    }
}
