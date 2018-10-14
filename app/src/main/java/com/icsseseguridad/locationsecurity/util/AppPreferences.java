package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.SystemClock;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.Watch;

public class AppPreferences {

    public static final String PREF_FILE_NAME = "app-security-v5";

    private static final int VALUE_GPS_UPDATE_DEFAULT = 300; // SECONDS (= 10 minutes)

    private static final String GUARD = "guard";
    private static final String WATCH = "watch";
    private static final String IMEI = "imei";
    private static final String DROP = "drop";
    private static final String ALERT = "alert";
    private static final String GPS_UPDATE = "gps_update";
    private static final String REGISTERED = "registered";
    private static final String LAST_SYNC = "last_sync";

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
            editor.putString(GUARD, gson().toJson(guard));
        }
        editor.commit();
    }

    public void setAlert(Alert alert) {
        editor = preferences.edit();
        if (alert == null) {
            editor.putString(ALERT, null);
        } else {
            editor.putString(ALERT, gson().toJson(alert));
        }
        editor.commit();
    }

    public Alert getAlert() {
        String json = preferences.getString(ALERT, null);
        if (json == null) {
            return null;
        }
        return gson().fromJson(json, Alert.class);
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
        return gson().fromJson(json, Guard.class);
    }

    public void setWatch(Watch watch) {
        editor = preferences.edit();
        if (watch == null) {
            editor.putString(WATCH, null);
        } else {
            editor.putString(WATCH, gson().toJson(watch));
        }
        editor.commit();
    }

    public Watch getWatch() {
        String json = preferences.getString(WATCH, null);
        if (json == null) {
            return null;
        }
        return gson().fromJson(json, Watch.class);
    }

    public void setImei(String imei) {
        if (imei != null) {
            preferences.edit().putString(IMEI, imei).commit();
        }
    }

    public void setRegistered() {
        preferences.edit().putBoolean(REGISTERED, true).commit();
    }

    public Boolean isRegistered() {
        return preferences.getBoolean(REGISTERED, false);
    }

    public String getImei() {
        return preferences.getString(IMEI, null);
    }

    public void setGpsUpdate(ConfigUtility update) {
        if (update != null) {
            preferences.edit().putString(GPS_UPDATE, update.value).commit();
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
            preferences.edit().putString("LOCATION_LAT", String.valueOf(location.getLatitude())).commit();
            preferences.edit().putString("LOCATION_LON", String.valueOf(location.getLongitude())).commit();
            preferences.edit().putString("LOCATION_PROVIDER", String.valueOf(location.getProvider())).commit();
            preferences.edit().putString("LOCATION_TIME", String.valueOf(location.getTime())).commit();
            return true;
        }
    }

    public Boolean isDrop() {
        Boolean drop = preferences.getBoolean(DROP, false);
        preferences.edit().putBoolean(DROP, false).commit();
        return drop;
    }

    public void setDrop() {
        preferences.edit().putBoolean(DROP, true).commit();
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
        preferences.edit().remove(GUARD).commit();
    }

    public void clearWatch() {
        preferences.edit().remove(GUARD).commit();
        preferences.edit().remove(WATCH).commit();
    }

    public void saveLastSync(long millis) {
        preferences.edit().putLong(LAST_SYNC, millis).commit();
    }

    public boolean canSync() {
        long lastSync = preferences.getLong(LAST_SYNC, 0);
        if (System.currentTimeMillis() > (lastSync + 59999)) {
            return true;
        } else {
            return false;
        }
    }

    private Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }
}
