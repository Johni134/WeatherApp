package ru.geekbrains.evgeniy.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollutionModel implements Parcelable {
    @SerializedName("precision")
    @Expose
    public Double precision;
    @SerializedName("pressure")
    @Expose
    public Double pressure;
    @SerializedName("value")
    @Expose
    public Double value;

    public String getPressureString() {
        return pressure.toString() + " hPa";
    }

    protected PollutionModel(Parcel in) {
        if (in.readByte() == 0) {
            precision = null;
        } else {
            precision = in.readDouble();
        }
        if (in.readByte() == 0) {
            pressure = null;
        } else {
            pressure = in.readDouble();
        }
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readDouble();
        }
    }

    public static final Creator<PollutionModel> CREATOR = new Creator<PollutionModel>() {
        @Override
        public PollutionModel createFromParcel(Parcel in) {
            return new PollutionModel(in);
        }

        @Override
        public PollutionModel[] newArray(int size) {
            return new PollutionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (precision == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(precision);
        }
        if (pressure == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pressure);
        }
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(value);
        }
    }
}
