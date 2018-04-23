package ru.geekbrains.evgeniy.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;

public class HistoryModel implements Parcelable {
    @SerializedName("dt")
    public Long dt;

    @SerializedName("main")
    public MainWheatherInfo main;

    @SerializedName("weather")
    public RealmList<WeatherModel> weather = new RealmList<>();

    @SerializedName("sys")
    public SystemModel sys;

    @SerializedName("dt_txt")
    public String dtTxt;

    protected HistoryModel(Parcel in) {
        if (in.readByte() == 0) {
            dt = null;
        } else {
            dt = in.readLong();
        }
        dtTxt = in.readString();

        //List<WeatherModel>
        in.readList(weather, WeatherModel.class.getClassLoader());

        // MainWheatherInfo
        if (in.readByte() == 0) {
            main = null;
        }
        else {
            main = new MainWheatherInfo();
            main.temp = in.readDouble();
            main.humidity = in.readLong();
            main.pressure = in.readDouble();
            main.temp_max = in.readDouble();
            main.temp_min = in.readDouble();
        }

        // SystemModel
        if (in.readByte() == 0) {
            sys = null;
        }
        else {
            sys = new SystemModel();
            sys.country = in.readString();
            sys.id = in.readLong();
            sys.sunrise = in.readLong();
            sys.sunset = in.readLong();
            sys.type = in.readLong();
            sys.message = in.readDouble();
        }
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dt);
        }
        dest.writeString(dtTxt);

        // List<WeatherModel>
        dest.writeList(weather);

        // MainWheatherInfo
        if (main == null) {
            dest.writeByte((byte) 0);
        }
        else {
            dest.writeByte((byte) 1);
            dest.writeDouble(main.temp);
            dest.writeLong(main.humidity);
            dest.writeDouble(main.pressure);
            dest.writeDouble(main.temp_max);
            dest.writeDouble(main.temp_min);
        }

        // SystemModel
        if (sys == null) {
            dest.writeByte((byte) 0);
        }
        else {
            dest.writeByte((byte) 1);
            dest.writeString(sys.country);
            dest.writeLong(sys.id);
            dest.writeLong(sys.sunrise);
            dest.writeLong(sys.sunset);
            dest.writeLong(sys.type);
            dest.writeDouble(sys.message);
        }
    }

}
