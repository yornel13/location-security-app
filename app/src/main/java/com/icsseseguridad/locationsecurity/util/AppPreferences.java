package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.Watch;

public class AppPreferences {

    public static final String PREF_FILE_NAME = "app-security-v3";

    private static final int VALUE_GPS_UPDATE_DEFAULT = 600; // SECONDS (= 10 minutes)

    private static final String GUARD = "guard";
    private static final String WATCH = "watch";
    private static final String IMEI = "imei";
    private static final String DROP = "drop";
    private static final String ALERT = "alert";
    private static final String GPS_UPDATE = "gps_update";
    private static final String REGISTERED = "registered";

    private Context context;
    private SharedPreferences preferences;
    private Editor editor;

    public AppPreferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
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

    public void setAlert(Alert alert) {
        editor = preferences.edit();
        if (alert == null) {
            editor.putString(ALERT, null);
        } else {
            editor.putString(ALERT, new Gson().toJson(alert));
        }
        editor.apply();
    }

    public Alert getAlert() {
        String json = preferences.getString(ALERT, null);
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, Alert.class);
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

    public void setImei(String imei) {
        if (imei != null) {
            preferences.edit().putString(IMEI, imei).apply();
        }
    }

    public void setRegistered() {
        preferences.edit().putBoolean(REGISTERED, true).apply();
    }

    public Boolean isRegistered() {
        return preferences.getBoolean(REGISTERED, false);
    }

    public String getImei() {
        return preferences.getString(IMEI, null);
    }

    public void setGpsUpdate(ConfigUtility update) {
        if (update != null) {
            preferences.edit().putString(GPS_UPDATE, update.value).apply();
        }
    }

    public Long getGPSUpdate() {
        String gpsValue = preferences.getString(GPS_UPDATE, null);
        if (gpsValue == null) {
            return (long) (VALUE_GPS_UPDATE_DEFAULT * 1000);
        }
        return (long) (Integer.valueOf(gpsValue) * 1000);
    }

    public boolean setLastKnownLoc(Location location) {
        if (location == null) {
            return false;
        } else {
            preferences.edit().putString("LOCATION_LAT", String.valueOf(location.getLatitude())).apply();
            preferences.edit().putString("LOCATION_LON", String.valueOf(location.getLongitude())).apply();
            preferences.edit().putString("LOCATION_PROVIDER", String.valueOf(location.getProvider())).apply();
            preferences.edit().putString("LOCATION_TIME", String.valueOf(location.getTime())).apply();
            return true;
        }
    }

    public Boolean isDrop() {
        Boolean drop = preferences.getBoolean(DROP, false);
        preferences.edit().putBoolean(DROP, false).apply();
        return drop;
    }

    public void setDrop() {
        preferences.edit().putBoolean(DROP, true).apply();
    }

    public Location getLastKnownLoc() {
        String lat = preferences.getString("LOCATION_LAT", null);
        String lon = preferences.getString("LOCATION_LON", null);
        Location location = null;
        if (lat != null && lon != null) {
            String provider = preferences.getString("LOCATION_PROVIDER", null);
            String time = preferences.getString("LOCATION_TIME", null);
            location = new Location(provider);
            location.setLatitude(Double.parseDouble(lat));
            location.setLongitude(Double.parseDouble(lon));
            location.setTime(Long.valueOf(time));
        }
        return location;
    }

    public void clearAll() {
        preferences.edit().remove(GUARD).apply();
    }

    public void clearWatch() {
        preferences.edit().remove(GUARD).apply();
        preferences.edit().remove(WATCH).apply();
    }

}
