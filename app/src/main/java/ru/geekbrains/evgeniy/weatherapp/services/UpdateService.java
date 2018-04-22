package ru.geekbrains.evgeniy.weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.WeatherApplication;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;

public class UpdateService extends Service {

    private UpdateWeathersBinder binder = new UpdateWeathersBinder();

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
        Realm realm = Realm.getInstance(WeatherApplication.getRealmConf());
        DataHelper.updateAllWeathers(realm);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
