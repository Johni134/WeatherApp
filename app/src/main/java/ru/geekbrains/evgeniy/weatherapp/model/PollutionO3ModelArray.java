package ru.geekbrains.evgeniy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollutionO3ModelArray {

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("location")
    @Expose
    public CoordModel location;

    @SerializedName("data")
    @Expose
    public float data;

}
