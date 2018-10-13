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

    public static String longToString(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
    }

    public static Date stringToDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
