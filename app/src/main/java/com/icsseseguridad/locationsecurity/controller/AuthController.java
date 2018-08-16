package com.icsseseguridad.locationsecurity.controller;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnSignInFailure;
import com.icsseseguridad.locationsecurity.events.OnSignInSuccess;
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
}
