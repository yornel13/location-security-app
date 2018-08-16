package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
}
