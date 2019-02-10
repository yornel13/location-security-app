package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UTILITY {

    public static Typeface typefaceBar(Context context){
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/GoogleSans-Regular.ttf" );
    }

    public static Typeface typefaceEditText(Context context){
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/Rubik-Regular.ttf" );
    }

    public static Typeface typefaceButton(Context context){
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/Raleway-SemiBold.ttf" );
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(calendar.getTime());
    }

    public static String getCurrentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        return getFormat().format(calendar.getTime());
    }

    public static Date stringToDate(String time) {
        try {
            return getFormat(time).parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SimpleDateFormat getFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return dateFormat;
    }

    public static SimpleDateFormat getFormat(String date) {
        SimpleDateFormat dateFormat;
        if (date.contains("T")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return dateFormat;
    }
}
