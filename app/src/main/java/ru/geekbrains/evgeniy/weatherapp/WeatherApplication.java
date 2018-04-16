package ru.geekbrains.evgeniy.weatherapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
