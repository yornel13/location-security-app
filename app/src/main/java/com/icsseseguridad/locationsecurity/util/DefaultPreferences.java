package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.icsseseguridad.locationsecurity.model.ConfigUtility;

public class DefaultPreferences {

    private static final String IMEI = "imei";
    private static final String WATCH_ID = "watch_id";
    private static final String GPS_UPDATE = "gps_update";

    private static final int VALUE_GPS_UPDATE_DEFAULT = 600; // SECONDS (= 10 minutes)

    SharedPreferences preferences;

    public DefaultPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setImei(String imei) {
        if (imei != null) {
            preferences.edit().putString(IMEI, imei).apply();
        }
    }

    public String getImei() {
        return preferences.getString(IMEI, null);
    }

    public void setWatchId(Long watchId) {
        if (watchId != null) {
            preferences.edit().putLong(WATCH_ID, watchId).apply();
        }
    }

    public Long getWatchId() {
        return preferences.getLong(WATCH_ID, 0);
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

    public void clearWatch() {
        preferences.edit().remove(WATCH_ID).apply();
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
}
