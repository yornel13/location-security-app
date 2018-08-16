package com.icsseseguridad.locationsecurity.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppDatetime {

    public static String longDatetime(Long millis) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(millis);
        df = new SimpleDateFormat("h:mm a");
        String time = df.format(millis);
        return date+" a las "+time;
    }

    public static String longTime(Long millis) {
        DateFormat df = new SimpleDateFormat("h:mm a");
        String time = df.format(millis);
        return "a las "+time;
    }

    public static String date(Long millis) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(millis);
        return date;
    }
}
