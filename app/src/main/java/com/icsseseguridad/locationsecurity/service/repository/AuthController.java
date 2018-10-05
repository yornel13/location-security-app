package com.icsseseguridad.locationsecurity.service.repository;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.entity.Tablet;
import com.icsseseguridad.locationsecurity.service.event.OnRegisteredTabletFailure;
import com.icsseseguridad.locationsecurity.service.event.OnRegisteredTabletSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSignAdminSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSignInFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSignInSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnVerifySessionFailure;
import com.icsseseguridad.locationsecurity.service.event.OnVerifySessionSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.event.OnVerifyTabletFailure;
import com.icsseseguridad.locationsecurity.service.event.OnVerifyTabletSuccess;

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

    public void verifyTablet(String imei) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        System.out.println(preferences.getToken());
        Call<MultipleResource> call = apiInterface.verifyTablet(imei);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnVerifyTabletFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        System.out.println(new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnVerifyTabletFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnVerifyTabletFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnVerifyTabletFailure(resource.message));
                } else {
                    Tablet tablet = gson.fromJson(gson.toJson(resource.result), Tablet.class);
                    EventBus.getDefault().postSticky(new OnVerifyTabletSuccess(tablet));
                }
            }
        });
    }

    public void verify() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        System.out.println(preferences.getToken());
        Call<Guard> call = apiInterface.verifySession(preferences.getToken());
        call.enqueue(new Callback<Guard>() {
            @Override
            public void onFailure(Call<Guard> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnVerifySessionFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<Guard> call, Response<Guard> response) {
                if (!response.isSuccessful()) {
                    if (response.code() != 401)
                        EventBus.getDefault().postSticky(new OnVerifySessionFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnVerifySessionSuccess(response.body()));
                }
            }
        });
    }
}
