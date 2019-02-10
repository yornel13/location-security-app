package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.repository.APIClient;
import com.icsseseguridad.locationsecurity.service.repository.APIInterface;

import retrofit2.Call;
import retrofit2.Response;

public class SendAlert {

    private static final String TAG = "SendAlert";

    public SendAlert() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public boolean send(String token, Alert alert) {
        System.out.println(new Gson().toJson(alert));
        boolean isSuccess = false;
        if (alert != null)
            try {
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<MultipleResource> call = apiInterface.syncAlert(token, alert);
                Response<MultipleResource> tasks = call.execute();
                MultipleResource data = tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    isSuccess = true;
                } else {
                    Log.e(TAG, "Can't send alert now");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return isSuccess;
    }
}
