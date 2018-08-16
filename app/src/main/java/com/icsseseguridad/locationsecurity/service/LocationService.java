package com.icsseseguridad.locationsecurity.service;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.controller.TabletPositionController;
import com.icsseseguridad.locationsecurity.model.ConfigUtility;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.TabletPosition;
import com.icsseseguridad.locationsecurity.model.Watch;
import com.icsseseguridad.locationsecurity.ui.LoginActivity;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.DefaultPreferences;

import java.util.concurrent.TimeUnit;

public class LocationService extends Service {

    private static final int NOTIFICATION_ID = 123;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            if (SecurityApp.getAppContext() != null) {
                Boolean done = new DefaultPreferences(SecurityApp.getAppContext()).setLastKnownLoc(location);
                Log.e(TAG, "saved: "+done.toString());
            } else
                Log.e(TAG, "can't save position");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        initialize();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (handler != null && runnableUpdate != null)
            handler.removeCallbacks(runnableUpdate);
        NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notify != null)
            notify.cancel(NOTIFICATION_ID);
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    public void initialize() {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "locationSecurity")
                .setSmallIcon(R.drawable.gps_signal)
                .setContentText("Servicio de Localización");
        builder.setOngoing(true);

        Intent i = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        if (SecurityApp.getAppContext() != null) {
            Long delayMillis = new DefaultPreferences(SecurityApp.getAppContext()).getGPSUpdate();
            handler.postDelayed(runnableUpdate, delayMillis);
        } else {
            Log.e(TAG, "can't get gps update time");
            handler.postDelayed(runnableUpdate, 10*1000);
        }
    }

    Handler handler = new Handler();
    // Update tablet position
    private Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            Log.d("Handlers", "checking gps location");
            if (SecurityApp.getAppContext() != null) {
                Long delayMillis = new DefaultPreferences(SecurityApp.getAppContext()).getGPSUpdate();
                final Location location = new DefaultPreferences(SecurityApp.getAppContext()).getLastKnownLoc();
                final String imei = new DefaultPreferences(SecurityApp.getAppContext()).getImei();
                final Long watchId = new DefaultPreferences(SecurityApp.getAppContext()).getWatchId();
                Log.d("Handlers", location.toString());
                if (watchId != 0) {
                    new Handler(LocationService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            savePosition(location, imei, watchId);
                        }
                    });
                    handler.postDelayed(this, delayMillis);
                } else {
                    Log.d("Handlers", "No watch active! close handler");
                    new Handler(LocationService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            LocationService.this.stopSelf();
                        }
                    });
                }
            } else {
                Log.e("Handlers", "can't get gps update time");
                handler.postDelayed(this, 10*1000);
            }
        }
    };

    public void savePosition(Location location, String imei, Long watchId) {
        Log.d("savePosition", "trying to save position");
        TabletPositionController positionController = new TabletPositionController();
        TabletPosition tabletPosition = new TabletPosition(location, imei);
        tabletPosition.watchId = watchId;
        positionController.register(tabletPosition, false);
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
