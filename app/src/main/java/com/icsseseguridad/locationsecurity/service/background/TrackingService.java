package com.icsseseguridad.locationsecurity.service.background;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.synchronizer.AlertSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.MainSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.SavePositionJob;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;
import com.icsseseguridad.locationsecurity.view.ui.LoginActivity;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 7788;

    private FusedLocationProviderClient client;

//    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            unregisterReceiver(stopReceiver);
//            stopSelf();
//        }
//    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (CurrentLocation.getPreferences(this).getWatch() != null) {
            Log.e(TAG, "onStartCommand");
            buildNotification();
            requestLocationUpdates();
            // Main sync job
            MainSyncJob.jobScheduler(this);
            // Running save position job
            SavePositionJob.jobScheduler(this);
            // Running alert sync job if need
            AlertSyncJob.jobScheduler(this);
            return START_STICKY;
        } else {
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (client != null)
            client.removeLocationUpdates(callback);
        stopForeground(true);
    }

    private void buildNotification() {
        String stop = "stop";
        //registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, createNotificationChannel(notificationManager));
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.gps_signal);
        builder.setOngoing(true);
        Intent i = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        builder.setContentIntent(pendingIntent);
        startForeground(NOTIFICATION_ID, builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager mNotificationManager) {
        String channelId = "tacking_service";
        String channelName = "Background Tacking Service";
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setSound(null, null);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, callback, null);
        }
    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(TAG, String.valueOf(location.getLatitude())
                        + ", " + String.valueOf(location.getLongitude()));
                new AppPreferences(getApplicationContext()).setLastKnownLoc(location);
            }
        }
    };

    public static void start(Context context) {
        // Do nothing if service is already running
        if (isServiceRunning(TrackingService.class, context)) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, TrackingService.class));
        } else {
            context.startService(new Intent(context, TrackingService.class));
        }
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
