package com.icsseseguridad.locationsecurity.service.background;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.synchronizer.SyncAdapter;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.view.ui.LoginActivity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RepoIntentService extends IntentService {

    private static final String TAG = "RepoIntentService";

    private static final int NOTIFICATION_ID = 6655;

    private static long INTERVAL_ALARM = TimeUnit.MINUTES.toMillis(5);

    private SyncAdapter syncAdapter;

    public RepoIntentService() {
        super(TAG);
    }

    public static boolean reSync = false;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        syncAdapter = new SyncAdapter(this);
        completeWork();
    }

    public void completeWork() {
        Log.d(TAG, "Starting work");
        reSync = false;
        syncAdapter.sync();
        if (reSync) {
            completeWork();
            return;
        }
        stopSelf();
        if (syncAdapter.needSync()) {
            setAlarm(this, TimeUnit.SECONDS.toMillis(60));
        } else {
            if (new AppPreferences(getApplicationContext()).getWatch() != null) {
                setAlarm(this, INTERVAL_ALARM);
            } else {
                Log.d(TAG, "No set alarm, is no active watch");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
    }

    public static void run(Context context) {
        cancelAlarm(context);
        if (isRunning(context)) {
            reSync = true;
        } else {
            Intent intent = new Intent(context, RepoIntentService.class);
            context.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "All work complete");
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
                .setContentText(getString(R.string.sync_data))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.sync_icon);
        builder.setOngoing(true);
        builder.setAutoCancel(false);
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

    public static void setAlarm(Context context, long interval) {
        Log.d(TAG, "Calling alarm");
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Intent myAlarm = new Intent(context.getApplicationContext(), RepoAlarmReceiver.class);
        PendingIntent alarmIntent  = PendingIntent.getBroadcast(context.getApplicationContext(), 1, myAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + interval, alarmIntent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context.getApplicationContext(), RepoAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 1, myIntent, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }

    private static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RepoIntentService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
