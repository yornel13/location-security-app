package com.icsseseguridad.locationsecurity.service.repository;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnFinishWatchFailure;
import com.icsseseguridad.locationsecurity.service.event.OnFinishWatchSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnInitWatchFailure;
import com.icsseseguridad.locationsecurity.service.event.OnInitWatchSuccess;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.Watch;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchController extends BaseController {

    public void register(String authorization, Long guardId, String latitude, String longitude, @Nullable String imei) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.initWatch(authorization, guardId, latitude, longitude, imei);
        System.out.println(preferences.getToken() + " " + guardId + " " +  latitude + " " +  longitude + " " +  imei);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnInitWatchFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        System.out.println(new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnInitWatchFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnInitWatchFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                System.out.println(new Gson().toJson(resource));
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnInitWatchFailure(resource.message));
                } else {
                    Watch watch = gson.fromJson(gson.toJson(resource.result), Watch.class);
                    EventBus.getDefault().postSticky(new OnInitWatchSuccess(watch));
                }
            }
        });
    }

    public void finish(Long watchId, String latitude, String longitude, @Nullable String observation) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.finishWatch(preferences.getToken(), watchId, latitude, longitude, observation);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnFinishWatchFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.errorBody().string();
                        System.out.println(errorBody);
                        MultipleResource resource = gson.fromJson(errorBody, MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnFinishWatchFailure(resource));
                    } catch (Exception e) {
                        EventBus.getDefault().postSticky(new OnFinishWatchFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnFinishWatchFailure(resource));
                } else {
                    EventBus.getDefault().postSticky(new OnFinishWatchSuccess(resource));
                }
            }
        });
    }
}