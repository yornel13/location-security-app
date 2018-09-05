package com.icsseseguridad.locationsecurity.controller;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnRegisteredTabletFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisteredTabletSuccess;
import com.icsseseguridad.locationsecurity.events.OnSendAlertFailure;
import com.icsseseguridad.locationsecurity.events.OnSendAlertSuccess;
import com.icsseseguridad.locationsecurity.events.OnSignAdminSuccess;
import com.icsseseguridad.locationsecurity.events.OnSignInFailure;
import com.icsseseguridad.locationsecurity.events.OnSignInSuccess;
import com.icsseseguridad.locationsecurity.model.Admin;
import com.icsseseguridad.locationsecurity.model.Alert;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.MultipleResource;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthController extends BaseController {

    public void singIn(String dni, String password) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.signIn(dni, password);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnSignInFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection))
                );
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnSignInFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnSignInFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (resource.response) {
                    Guard guard = gson.fromJson(gson.toJson(resource.result), Guard.class);
                    EventBus.getDefault().postSticky(new OnSignInSuccess(guard));
                } else {
                    EventBus.getDefault().postSticky(new OnSignInFailure(resource.message));
                }
            }
        });
    }

    public void singInAdmin(String dni, String password) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.signInAdmin(dni, password);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnSignInFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection))
                );
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnSignInFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnSignInFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (resource.response) {
                    String token = gson.fromJson(gson.toJson(resource.result), String.class);
                    EventBus.getDefault().postSticky(new OnSignAdminSuccess(token));
                } else {
                    EventBus.getDefault().postSticky(new OnSignInFailure(resource.message));
                }
            }
        });
    }

    public void registeredTablet(String imei) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.registered(imei);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnRegisteredTabletFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        System.out.println(new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnRegisteredTabletFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnRegisteredTabletFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnRegisteredTabletFailure(resource.message));
                } else {
                    EventBus.getDefault().postSticky(new OnRegisteredTabletSuccess(resource));
                }
            }
        });
    }
}
