package com.icsseseguridad.locationsecurity.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

public class CurrentLocation {

    @SuppressLint("MissingPermission")
    public static Location get(Context context) {
        return get(context, true);
    }
    /*
     *  Call this method synchronously
     */
    @SuppressLint("MissingPermission")
    public static Location get(Context context, boolean consultPreferences) {
        final TaskCompletionSource<Location> tcs = new TaskCompletionSource<>();
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            Log.d("Completed", String.valueOf(location.getLatitude())
                                    + ", " + String.valueOf(location.getLongitude()));
                            tcs.setResult(location);
                        } else {
                            tcs.setException(new Exception());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Completed", "Failure");
                tcs.setException(new Exception());
            }
        });
        Task<Location> t = tcs.getTask();
        try {
            Tasks.await(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t.isSuccessful()) {
            return t.getResult();
        } else {
            if (consultPreferences)
                return getPreferences(context).getLastKnownLoc();
            else
                return null;
        }
    }

    public static AppPreferences getPreferences(Context context) {
        return new AppPreferences(context);
    }
}
