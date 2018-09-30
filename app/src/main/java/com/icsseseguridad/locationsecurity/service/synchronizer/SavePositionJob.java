package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;

import java.sql.Timestamp;
import java.util.Date;

public class SavePositionJob extends JobService {

    private static final String TAG = "SavePositionJob";
    private static final int JOB_ID = 1077;

//    private static boolean isFirstTime = false;
//
//    private static final long REFRESH_INTERVAL  = 60 * 1000;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "Starting Synchronization Job");
        new Thread(new Runnable() {
            @Override
            public void run() {
                completeJob(params);
            }
        }).start();
        return true;
    }

    public void completeJob(final JobParameters parameters) {
//        if (isFirstTime) { // do not start job immediately
//            isFirstTime = false;
//        } else {
            Log.d(TAG, "Running work");
            if ( new AppPreferences(getApplicationContext()).getWatch() == null) {
                jobFinished(parameters, false);
                return;
            }
            savePosition();
//        }
        jobFinished(parameters, true);
    }

    public void savePosition() {
        Location location = CurrentLocation.get(this);
        String imei = CurrentLocation.getPreferences(this).getImei();
        Long watchId = CurrentLocation.getPreferences(this).getWatch().id;
        final TabletPosition position = new TabletPosition(location, imei);
        position.generatedTime = new Timestamp(new Date().getTime());
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

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Synchronization job was stopped");
        return false;
    }

    public static void jobScheduler(Context context) {
        Log.d(TAG, "Creating Synchronization Job");
//        isFirstTime = true;
        long interval = new AppPreferences(context).getGPSUpdate();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, SavePositionJob.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(interval)
                    .setOverrideDeadline(interval + 10)
                    .build();
        if (jobScheduler == null)
            return;
        jobScheduler.schedule(jobInfo);
    }

    public static void cancelJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) ;
        assert scheduler != null;
        scheduler.cancel(JOB_ID);
    }
}
