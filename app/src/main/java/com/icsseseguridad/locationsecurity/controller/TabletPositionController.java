package com.icsseseguridad.locationsecurity.controller;

import android.util.Log;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesFailure;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesSuccess;
import com.icsseseguridad.locationsecurity.events.OnGetUpdateGpsFailure;
import com.icsseseguridad.locationsecurity.events.OnGetUpdateGpsSuccess;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportFailure;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportSuccess;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceFailure;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceSuccess;
import com.icsseseguridad.locationsecurity.events.OnPostPositionFailure;
import com.icsseseguridad.locationsecurity.events.OnPostPositionSuccess;
import com.icsseseguridad.locationsecurity.events.OnPostReplyFailure;
import com.icsseseguridad.locationsecurity.events.OnPostReplySuccess;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportSuccess;
import com.icsseseguridad.locationsecurity.model.ConfigUtility;
import com.icsseseguridad.locationsecurity.model.ListIncidence;
import com.icsseseguridad.locationsecurity.model.ListReply;
import com.icsseseguridad.locationsecurity.model.ListReport;
import com.icsseseguridad.locationsecurity.model.MultipleResource;
import com.icsseseguridad.locationsecurity.model.Reply;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.model.TabletPosition;

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
                position.getMessageTimeString());
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
