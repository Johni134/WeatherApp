package ru.geekbrains.evgeniy.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollutionNo2Model implements Parcelable {
    @SerializedName("no2")
    @Expose
    public PollutionModel no2;
    @SerializedName("no2_strat")
    @Expose
    public PollutionModel no2Strat;
    @SerializedName("no2_trop")
    @Expose
    public PollutionModel no2Trop;

    protected PollutionNo2Model(Parcel in) {
        no2 = in.readParcelable(PollutionModel.class.getClassLoader());
        no2Strat = in.readParcelable(PollutionModel.class.getClassLoader());
        no2Trop = in.readParcelable(PollutionModel.class.getClassLoader());
    }

    public static final Creator<PollutionNo2Model> CREATOR = new Creator<PollutionNo2Model>() {
        @Override
        public PollutionNo2Model createFromParcel(Parcel in) {
            return new PollutionNo2Model(in);
        }

        @Override
        public PollutionNo2Model[] newArray(int size) {
            return new PollutionNo2Model[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(no2, flags);
        dest.writeParcelable(no2Strat, flags);
        dest.writeParcelable(no2Trop, flags);
    }
}
