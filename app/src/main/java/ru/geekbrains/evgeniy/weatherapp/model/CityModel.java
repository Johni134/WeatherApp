package ru.geekbrains.evgeniy.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CityModel implements Parcelable {
    private String name;
    private Integer temp;

    public CityModel(String name, Integer temp) {
        this.name = name;
        this.temp = temp;
    }

    public CityModel(String name) {
        this.name = name;
        temp = (int)(Math.random()*100 - 50);
    }

    protected CityModel(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            temp = null;
        } else {
            temp = in.readInt();
        }
    }

    public static final Creator<CityModel> CREATOR = new Creator<CityModel>() {
        @Override
        public CityModel createFromParcel(Parcel in) {
            return new CityModel(in);
        }

        @Override
        public CityModel[] newArray(int size) {
            return new CityModel[size];
        }
    };

    public Integer getTemp() {
        return temp;
    }

    public String getTempC() {
        return temp.toString() + "\u00B0";
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (temp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(temp);
        }
    }
}
