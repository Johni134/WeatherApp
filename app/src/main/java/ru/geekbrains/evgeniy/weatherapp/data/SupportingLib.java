package ru.geekbrains.evgeniy.weatherapp.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SupportingLib {

    public static final String EXTRA_CITY_ID = "EXTRA_CITY_ID";
    public static final String EXTRA_CITY_TITLE = "EXTRA_CITY_TITLE";
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LON = "EXTRA_LON";

    public static String getLastUpdate(Long dt) {

        long time = dt * (long) 1000;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("GMT"));

        return format.format(date);
    }

    public static String getTime(Long dt) {
        long time = dt * (long) 1000;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

}
