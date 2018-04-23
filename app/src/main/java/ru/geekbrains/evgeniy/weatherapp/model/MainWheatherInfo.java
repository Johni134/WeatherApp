package ru.geekbrains.evgeniy.weatherapp.model;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;


public class MainWheatherInfo extends RealmObject {

    @SerializedName("temp")
    public Double temp;

    public Double pressure;
    public long humidity;
    public Double temp_min;
    public Double temp_max;

    public String getTempC() {
        return String.format("%.2f", temp) + " ℃";
    }
}
