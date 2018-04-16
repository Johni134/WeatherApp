package ru.geekbrains.evgeniy.weatherapp.model;

import io.realm.RealmObject;

public class SystemModel extends RealmObject {
    public long type;
    public long id;
    public double message;
    public String country;
    public long sunrise;
    public long sunset;
}
