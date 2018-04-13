package ru.geekbrains.evgeniy.weatherapp.model;


import com.google.gson.annotations.SerializedName;


public class MainWheatherInfo {

    @SerializedName("temp")
    public Double temp;

    public Double pressure;
    public long humidity;
    public Double temp_min;
    public Double temp_max;
}
