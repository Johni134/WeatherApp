package ru.geekbrains.evgeniy.weatherapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.geekbrains.evgeniy.weatherapp.services.UpdateService;

public class WeatherApplication extends Application {

    private int SCHEMA_VERSION = 2;

    private static RealmConfiguration config;

    // service
    private boolean shouldUnbind = false;
    private ServiceConnection serviceConnection;
    private UpdateService updateService;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Realm.init(this);

        config = new RealmConfiguration
                .Builder()
                .schemaVersion(SCHEMA_VERSION) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .name("myrealm.realm").build();
        Realm.setDefaultConfiguration(config);

        // init service
        initService();
    }

    public static RealmConfiguration getRealmConf() {
        return config;
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                updateService = ((UpdateService.UpdateWeathersBinder) service).getService();
                shouldUnbind = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                shouldUnbind = false;
            }
        };
        onBindService();
    }

    public void onBindService() {
        Intent intent = new Intent(getBaseContext(), UpdateService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    public void onUnbindService() {
        if(shouldUnbind) {
            unbindService(serviceConnection);
        }
        shouldUnbind = false;
    }

    public void updateWeathers() {
        if(!shouldUnbind) {
            Toast.makeText(this, getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
            onBindService();
            return;
        }

        updateService.updateWeathers();
    }

    @Override
    public void onTerminate() {
        onUnbindService();
        super.onTerminate();
    }
}
