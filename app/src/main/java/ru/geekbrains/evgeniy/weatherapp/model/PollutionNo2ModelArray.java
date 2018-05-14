package ru.geekbrains.evgeniy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PollutionNo2ModelArray {

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("location")
    @Expose
    public CoordModel location;

    @SerializedName("data")
    @Expose
    public PollutionNo2Model pollutionNo2Model;

}
