package ru.geekbrains.evgeniy.weatherapp.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SupportingLib {

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
