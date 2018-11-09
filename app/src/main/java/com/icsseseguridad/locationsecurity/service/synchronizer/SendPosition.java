package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.repository.APIClient;
import com.icsseseguridad.locationsecurity.service.repository.APIInterface;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import retrofit2.Call;
import retrofit2.Response;

public class SendPosition {

    private static final String TAG = "PositionIntentService";

    public SendPosition() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public boolean sync(TabletPosition position, AppPreferences preferences) {
        boolean isSuccess = false;
        if (position != null) {
            try {
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<MultipleResource> call = apiInterface.syncPosition(preferences.getToken(),
                        position.latitude,
                        position.longitude,
                        position.generatedTime,
                        position.messageTime,
                        position.watchId,
                        position.imei,
                        position.message,
                        position.alertMessage,
                        position.isException ? 1 : 0);
                Response<MultipleResource> tasks = call.execute();
                MultipleResource data = tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    isSuccess = true;
                    TabletPosition positionResponse = gson()
                            .fromJson(gson().toJson(data.result), TabletPosition.class);
                    Log.d(TAG, "-> Success sync position with id: " + positionResponse.id + " <-");
                } else {
                    Log.e(TAG, "Can't save position with id: " + position.id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    private Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }
}
