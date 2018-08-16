package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.model.ConfigUtility;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.Watch;

public class AppPreferences {

    public static final String PREF_FILE_NAME = "app-security-v1";

    private static final String GUARD = "guard";
    private static final String WATCH = "watch";

    private Context context;
    private SharedPreferences preferences;
    private Editor editor;

    public AppPreferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setGuard(Guard guard) {
        editor = preferences.edit();
        if (guard == null) {
            editor.putString(GUARD, null);
        } else {
            editor.putString(GUARD, new Gson().toJson(guard));
        }
        editor.apply();
    }

    public String getToken() {
        Guard guard = getGuard();
        if (guard != null)
            return guard.token;
        else
            return null;
    }

    public Guard getGuard() {
        String json = preferences.getString(GUARD, null);
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, Guard.class);
    }

    public void setWatch(Watch watch) {
        editor = preferences.edit();
        if (watch == null) {
            editor.putString(WATCH, null);
        } else {
            editor.putString(WATCH, new Gson().toJson(watch));
        }
        editor.apply();
    }

    public Watch getWatch() {
        String json = preferences.getString(WATCH, null);
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, Watch.class);
    }

    public void clearAll() {
        preferences.edit().remove(GUARD).apply();
    }

    public void clearGuard() {
        preferences.edit().remove(GUARD).apply();
    }

    public void clearWatch() {
        preferences.edit().remove(GUARD).apply();
        preferences.edit().remove(WATCH).apply();
    }

}
