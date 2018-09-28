package com.icsseseguridad.locationsecurity.service.repository;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnGetBannersFailure;
import com.icsseseguridad.locationsecurity.service.event.OnGetBannersSuccess;
import com.icsseseguridad.locationsecurity.service.entity.ListBanner;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerController extends BaseController {

    public void getBanners() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListBanner> call = apiInterface.getBanners(preferences.getToken());
        call.enqueue(new Callback<ListBanner>() {
            @Override
            public void onFailure(Call<ListBanner> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnGetBannersFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListBanner> call, Response<ListBanner> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnGetBannersFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnGetBannersSuccess(response.body()));
                }
            }
        });
    }
}
