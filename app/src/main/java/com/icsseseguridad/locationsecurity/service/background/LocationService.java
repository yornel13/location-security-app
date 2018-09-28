package com.icsseseguridad.locationsecurity.service.background;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.Watch;
import com.icsseseguridad.locationsecurity.view.ui.DropActivity;
import com.icsseseguridad.locationsecurity.view.ui.LoginActivity;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LocationService extends Service implements SensorEventListener {

    private static final int NOTIFICATION_ID = 123;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private double rootSquare;
    private boolean activated = false;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        rootSquare = Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
        if(rootSquare < 2.0 && !activated) {
            activated = true;
            Log.i("LocationService", "Drop is detected, sending alert");
            //Toast.makeText(this, "Fall detected", Toast.LENGTH_SHORT).show();
            if (new AppPreferences(getApplicationContext()).getWatch() != null) {
                new Handler(LocationService.this.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new AppPreferences(getApplicationContext()).setDrop();
                        Intent intent = new Intent(getApplicationContext(), DropActivity.class);
                        startActivity(intent);
                    }
                });
            }
            new Handler(LocationService.this.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    activated = false;
                }
            }, 30000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

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
            if (getApplicationContext() != null) {
                Boolean done = new AppPreferences(getApplicationContext()).setLastKnownLoc(location);
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
        initSensor();
        return START_STICKY;
    }

    public void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
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
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Servicio de Localización");
        builder.setOngoing(true);

        Intent i = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        if (SecurityApp.getAppContext() != null) {
            Long delayMillis = new AppPreferences(getApplicationContext()).getGPSUpdate();
            Location oldLocation = new AppPreferences(getApplicationContext()).getLastKnownLoc();
            if (oldLocation == null) {
                handler.postDelayed(runnableUpdate, 5*1000);
            } else {
                handler.postDelayed(runnableUpdate, delayMillis);
            }
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
            if (getApplicationContext() != null) {
                AppPreferences defaultPreferences = new AppPreferences(getApplicationContext());
                Long delayMillis = defaultPreferences.getGPSUpdate();
                final Location location = defaultPreferences.getLastKnownLoc();
                final String imei = defaultPreferences.getImei();
                final Watch watch = defaultPreferences.getWatch();
                if (watch != null) {
                    Log.d("Handlers", location.toString());
                    new Handler(LocationService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callSavePosition(location, imei, watch.id);
                        }
                    });
                    handler.postDelayed(this, delayMillis);
                } else {
                    Log.d("Handlers", "No watch active! close handler");
                    new Handler(LocationService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //LocationService.this.stopSelf();
                            handler.postDelayed(this, 10*1000);
                        }
                    });
                }
            } else {
                Log.e("Handlers", "can't get gps update time");
                handler.postDelayed(this, 10*1000);
            }
        }
    };

    public void callSavePosition(final Location location, final String imei, final Long watchId) {
        final TabletPosition position = new TabletPosition(location, imei);
        position.generatedTime = new Timestamp(new Date().getTime());
        position.watchId = watchId;
        position.isException = false;
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                savePosition(position);
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception { }
                }).isDisposed();
    }

    private void savePosition(TabletPosition position) {
        Log.d("savePosition", "trying to save position");
        position.id = AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().insert(position);
        if (position.id > 0) {
            Log.d("savePosition", "position saved");
        } else {
            Log.d("savePosition", "position save failure");
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
