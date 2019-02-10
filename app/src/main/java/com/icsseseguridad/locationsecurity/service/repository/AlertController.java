package com.icsseseguridad.locationsecurity.service.repository;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnSendAlertFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSendAlertSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertController extends BaseController {

    public void send(Alert alert) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.sendAlert(preferences.getToken(), alert);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnSendAlertFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        System.out.println(new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnSendAlertFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnSendAlertFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnSendAlertFailure(resource.message));
                } else {
                    Alert alertSend = gson.fromJson(gson.toJson(resource.result), Alert.class);
                    EventBus.getDefault().postSticky(new OnSendAlertSuccess(alertSend));
                }
            }
        });
    }


}
