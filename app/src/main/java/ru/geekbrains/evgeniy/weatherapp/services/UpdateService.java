package ru.geekbrains.evgeniy.weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.WeatherApplication;
import ru.geekbrains.evgeniy.weatherapp.data.DataHelper;
import ru.geekbrains.evgeniy.weatherapp.data.WeatherDataLoader;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;

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
        final Realm realm = Realm.getInstance(WeatherApplication.getRealmConf());
        final String ids = DataHelper.getIDsSync(realm);
        final Handler handler = new Handler();
        if(!ids.isEmpty()) {
            new Thread() {
                public void run() {
                    final CityModelArray cityModelArray = WeatherDataLoader.getListWeatherByIDs(ids);
                    if (cityModelArray != null && cityModelArray.list.size() > 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                DataHelper.updateAllWeathersByList(realm, cityModelArray.list);
                            }
                        });

                    }

                }
            }.start();
        }
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
