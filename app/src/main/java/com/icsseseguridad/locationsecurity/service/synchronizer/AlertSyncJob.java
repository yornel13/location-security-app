package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

public class AlertSyncJob extends JobService {

    private static final String TAG = "AlertSyncJob";
    private static final int JOB_ID = 1055;

    private static final long REFRESH_INTERVAL  = 10 * 1000;

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
        AppPreferences preferences = new AppPreferences(getApplicationContext());
        Alert alert = preferences.getAlert();
        Log.d(TAG, "Finished Job");
        boolean isSuccess = new SendAlert().send(preferences.getToken(), alert);
        jobFinished(parameters, true);
        if (isSuccess || alert == null) {
            new AppPreferences(getApplicationContext()).setAlert(null);
            Log.d(TAG, "Cancelling Job");
            cancelJob(getApplicationContext());
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Synchronization job was stopped");
        return false;
    }

    public static void jobScheduler(Context context) {
        Alert alert = new AppPreferences(context).getAlert();
        if (alert == null) {
            return;
        }
        jobScheduler(context, REFRESH_INTERVAL);
    }

    public static void jobScheduler(Context context, long interval) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, AlertSyncJob.class);
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setMinimumLatency(REFRESH_INTERVAL)
                    .setOverrideDeadline(1)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(REFRESH_INTERVAL)
                    .setMinimumLatency(0)
                    .build();
        }

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
