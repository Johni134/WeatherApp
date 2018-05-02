package ru.geekbrains.evgeniy.weatherapp.model;

import io.realm.RealmObject;

public class CoordModel extends RealmObject {

    public CoordModel() {
    }

    public CoordModel(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public float lon;
    public float lat;
}
