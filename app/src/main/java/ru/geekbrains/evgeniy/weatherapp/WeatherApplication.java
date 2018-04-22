package ru.geekbrains.evgeniy.weatherapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WeatherApplication extends Application {

    private int SCHEMA_VERSION = 2;

    private static RealmConfiguration config;

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
    }

    public static RealmConfiguration getRealmConf() {
        return config;
    }
}
