package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.icsseseguridad.locationsecurity.SecurityApp;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public class MainSyncJob extends JobService {

    private static final String TAG = "MainSyncJob";
    private static final int JOB_ID = 1001;

    private static final long REFRESH_INTERVAL  = 5 * 60 * 1000;

    private static SyncAdapter syncAdapter = null;

    private static boolean isSync = false;
    private static boolean reSync = false;

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapter(getApplication());
    }

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.d(TAG, "Starting Synchronization Job");
        if (!isSync) {
            isSync = true;
            Observable.fromCallable(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return callSync();
                }
            }).subscribeOn(((SecurityApp) getApplication())
                    .getBackgroundThread())
                    .subscribe();
        }
        return false;
    }

    public Boolean callSync() {
        Log.i(TAG, "Initiating Synchronization");
        syncAdapter.sync();
        if (reSync) {
            reSync = false;
            callSync();
        } else {
            isSync = false;
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "Synchronization job was stopped");
        return false;
    }

    public static void jobScheduler(Context context) {
        Log.d(TAG, "Calling job");
        if (isSync) {
            reSync = true;
            return;
        }
        Log.d(TAG, "it pass");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, MainSyncJob.class);
//        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setRequiresCharging(true)
//                .setPersisted(true)
//                .setPeriodic(REFRESH_INTERVAL)
//                .setMinimumLatency(1)
//                //.setOverrideDeadline(1)
//                .build();
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(true)
                    .setPersisted(true)
                    .setMinimumLatency(REFRESH_INTERVAL)
                    .setOverrideDeadline(1)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(true)
                    .setPersisted(true)
                    .setPeriodic(REFRESH_INTERVAL)
                    .setMinimumLatency(0)
                    .build();
        }
        if (jobScheduler == null) return;

        if (isJobServiceOn(context)) jobScheduler.cancel(JOB_ID);
        jobScheduler.schedule(jobInfo);
    }

    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) ;
        boolean hasBeenScheduled = false ;
        if (scheduler != null)
            for (JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
                if (jobInfo.getId() == JOB_ID) {
                    hasBeenScheduled = true ;
                    break ;
                }
            }

        return hasBeenScheduled ;
    }

    public static void cancelJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) ;
        assert scheduler != null;
        scheduler.cancel(JOB_ID);
    }
}