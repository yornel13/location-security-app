package com.icsseseguridad.locationsecurity.service.repository;

import android.util.Log;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnGetUpdateGpsFailure;
import com.icsseseguridad.locationsecurity.service.event.OnGetUpdateGpsSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnPostPositionFailure;
import com.icsseseguridad.locationsecurity.service.event.OnPostPositionSuccess;
import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabletPositionController extends BaseController {

    public void getUpdateGPS() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ConfigUtility> call = apiInterface.getUpdateGPS(preferences.getToken());
        call.enqueue(new Callback<ConfigUtility>() {
            @Override
            public void onFailure(Call<ConfigUtility> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnGetUpdateGpsFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ConfigUtility> call, Response<ConfigUtility> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnGetUpdateGpsFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnGetUpdateGpsSuccess(response.body()));
                }
            }
        });
    }

    public void register(TabletPosition position, boolean requestResponse) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.postPosition(preferences.getToken(),
                position.latitude,
                position.longitude,
                position.watchId,
                position.imei,
                position.message,
                position.messageTime);
        if (requestResponse)
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    EventBus.getDefault().postSticky(new OnPostPositionFailure(
                            new MultipleResource(false, SecurityApp
                                    .getAppContext().getString(R.string.error_connection))
                    ));
                }
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    if (!response.isSuccessful()) {
                        try {
                            MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                            EventBus.getDefault().postSticky(new OnPostPositionFailure(resource));
                        } catch (IOException e) {
                            EventBus.getDefault().postSticky(new OnPostPositionFailure(
                                    new MultipleResource(false, SecurityApp
                                            .getAppContext().getString(R.string.error_connection))
                            ));
                        }
                        return;
                    }
                    MultipleResource resource = response.body();
                    if (!resource.response) {
                        EventBus.getDefault().postSticky(new OnPostPositionFailure(resource));
                    } else {
                        TabletPosition postPosition = gson.fromJson(gson.toJson(resource.result), TabletPosition.class);
                        EventBus.getDefault().postSticky(new OnPostPositionSuccess(postPosition));
                    }
                }
            });
        else
            call.enqueue(new Callback<MultipleResource>() {
                @Override
                public void onFailure(Call<MultipleResource> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    Log.e("Controller", "Save Position Failure.");
                }
                @Override
                public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                    if (!response.isSuccessful()) {
                        Log.e("Controller", "Save Position Failure: " + response.message());
                        return;
                    }
                    Log.e("Controller", "Position save success.");
                }
            });
    }
}
