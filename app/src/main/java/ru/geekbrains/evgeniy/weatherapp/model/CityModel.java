package ru.geekbrains.evgeniy.weatherapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class CityModel extends RealmObject implements Parcelable {

    public static final String FIELD_ID = "id";

    public static final String SORT_ID = "sortId";

    public CoordModel coord;
    public RealmList<WeatherModel> weather = new RealmList<>();
    public String base;

    @PrimaryKey
    public Long id;

    public Long sortId;

    public int cod;
    public MainWheatherInfo main;
    public SystemModel sys;
    public long dt;

    @SerializedName("name")
    private String name;

    public CityModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    protected CityModel(Parcel in) {
        base = in.readString();
        if (in.readByte() == 0) {
            id = null;
        }
        else {
            id = in.readLong();
        }
        name = in.readString();
        cod = in.readInt();
        dt = in.readLong();
        // for classes
        // CoordModel
        if (in.readByte() == 0) {
            coord = null;
        }
        else {
            coord = new CoordModel();
            coord.lat = in.readFloat();
            coord.lon = in.readFloat();
        }
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(base);
        if (id == null) {
            dest.writeByte((byte) 0);
        }
        else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeInt(cod);
        dest.writeLong(dt);
        // for classes
        // CoordModel
        if (coord == null) {
            dest.writeByte((byte) 0);
        }
        else {
            dest.writeByte((byte) 1);
            dest.writeFloat(coord.lat);
            dest.writeFloat(coord.lon);
        }

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

    public String getTempC() {
        return String.format("%.2f", main.temp) + " ℃";
    }
}
