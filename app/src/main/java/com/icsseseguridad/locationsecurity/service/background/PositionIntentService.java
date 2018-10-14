package com.icsseseguridad.locationsecurity.service.background;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class PositionIntentService extends IntentService {

    private static final String TAG = "PositionIntentService";

    private static final int ALARM_ID = 425;

    public PositionIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (CurrentLocation.getPreferences(this).getWatch() == null) {
            stopSelf();
            return;
        }
        completeWork();
    }

    private void completeWork() {
        Log.d(TAG, "Starting work");
        savePosition();
        setupAlarm();
        stopSelf();
    }

    private void setupAlarm() {
        if (CurrentLocation.getPreferences(this).getWatch() != null) {
            long interval = new AppPreferences(this).getGPSUpdate();
            setAlarm(this, interval);
        }
    }

    private void savePosition() {
        /*** Check last insert ***/
        TabletPosition lastInsert = AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().getLastInsert();
        if (lastInsert != null) {
            Date lastInsertDate = UTILITY.stringToDate(lastInsert.generatedTime);
            if (lastInsertDate != null) {
                long lastInsertLong = lastInsertDate.getTime();
                if ((lastInsertLong + 10000) > new Date().getTime()) {
                    /*** Check if last insert was more than 10 seconds ***/
                    Log.d(TAG, "can't save position now, programing for later");
                    return;
                }
            }
        }
        /*** Check last insert ***/
        Location location = CurrentLocation.get(this);
        String imei = CurrentLocation.getPreferences(this).getImei();
        Long watchId = CurrentLocation.getPreferences(this).getWatch().id;
        final TabletPosition position = new TabletPosition(location, imei);
        position.generatedTime = UTILITY.longToString(new Date().getTime());
        position.watchId = watchId;
        position.isException = false;
        position.id = AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().insert(position);
        if (position.id > 0) {
            Log.d(TAG, "position saved");
        } else {
            Log.d(TAG, "position save failure");
        }
    }

    public static void run(Context context) {
        cancelAlarm(context);
        if (!isRunning(context)) {
            Intent intent = new Intent(context, PositionIntentService.class);
            context.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "All work complete");
    }

    public static void setAlarm(Context context, long interval) {
        Log.d(TAG, "Calling alarm");
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Intent myAlarm = new Intent(context.getApplicationContext(), PositionAlarmReceiver.class);
        PendingIntent alarmIntent  = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_ID, myAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + interval, alarmIntent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context.getApplicationContext(), PositionAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), ALARM_ID, myIntent, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }

    private static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PositionIntentService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAlarmActive(Context context) {
        Intent intent = new Intent(context, PositionAlarmReceiver.class);
        return (PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }
}
