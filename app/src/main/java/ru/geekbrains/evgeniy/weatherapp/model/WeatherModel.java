package ru.geekbrains.evgeniy.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherModel implements Parcelable {
    public long id;
    public String main;
    public String description;
    public String icon;

    protected WeatherModel(Parcel in) {
        id = in.readLong();
        main = in.readString();
        description = in.readString();
        icon = in.readString();
    }

    public static final Creator<WeatherModel> CREATOR = new Creator<WeatherModel>() {
        @Override
        public WeatherModel createFromParcel(Parcel in) {
            return new WeatherModel(in);
        }

        @Override
        public WeatherModel[] newArray(int size) {
            return new WeatherModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(main);
        dest.writeString(description);
        dest.writeString(icon);
    }
}
