package com.icsseseguridad.locationsecurity.service.background;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.icsseseguridad.locationsecurity.service.entity.Watch;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!isServiceRunning(LocationService.class, context)) {
                Watch watch = new AppPreferences(context).getWatch();
                if (watch != null) {
                    Intent myIntent = new Intent(context, LocationService.class);
                    context.startService(myIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}