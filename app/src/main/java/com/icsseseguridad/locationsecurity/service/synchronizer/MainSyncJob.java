package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.icsseseguridad.locationsecurity.util.AppPreferences;

import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainSyncJob extends JobService {

    private static final String TAG = "MainSyncJob";
    private static final int JOB_ID = 1001;

    private static final long REFRESH_INTERVAL  = 5 * 60 * 1000;

    private static SyncAdapter syncAdapter = null;

    public static boolean isSync = false;
    private static boolean reSync = false;

    public static Subject<Boolean> syncObservable = PublishSubject.create();

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapter(getApplication());
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "Starting Synchronization Job");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isSync = true;
                    syncObservable.onNext(true);
                    completeJob(params);
                    isSync = false;
                    syncObservable.onNext(false);
                }
            }).start();
        return true;
    }

    public void completeJob(final JobParameters parameters) {
        Log.d(TAG, "Initiating Synchronization");
        if (new AppPreferences(getApplicationContext()).getWatch() == null) {
            if (syncAdapter.needSync()) {
                syncAdapter.sync();
            }
            if (!syncAdapter.needSync()) {
                jobFinished(parameters, false);
                if (isJobServiceOn(getApplicationContext())) cancelJob(getApplicationContext());
            } else {
                jobFinished(parameters, true);
            }
            reSync = false;
            return;
        }
        syncAdapter.sync();
        if (reSync) {
            reSync = false;
            completeJob(parameters);
        } else {
            jobFinished(parameters, false);
        }
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
        Log.d(TAG, "Creating job");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, MainSyncJob.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPeriodic(TimeUnit.MINUTES.toMillis(5))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build();
        if (jobScheduler == null) return;
        if (isJobServiceOn(context)) cancelJob(context);
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