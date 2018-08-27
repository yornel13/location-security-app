package com.icsseseguridad.locationsecurity.controller;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnAddClerkFailure;
import com.icsseseguridad.locationsecurity.events.OnAddClerkSuccess;
import com.icsseseguridad.locationsecurity.events.OnAddVehicleFailure;
import com.icsseseguridad.locationsecurity.events.OnAddVehicleSuccess;
import com.icsseseguridad.locationsecurity.events.OnAddVisitorFailure;
import com.icsseseguridad.locationsecurity.events.OnAddVisitorSuccess;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitSuccess;
import com.icsseseguridad.locationsecurity.events.OnGetBannersFailure;
import com.icsseseguridad.locationsecurity.events.OnGetBannersSuccess;
import com.icsseseguridad.locationsecurity.events.OnListActiveVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnListActiveVisitSuccess;
import com.icsseseguridad.locationsecurity.events.OnListClerkFailure;
import com.icsseseguridad.locationsecurity.events.OnListClerkSuccess;
import com.icsseseguridad.locationsecurity.events.OnListCompanyFailure;
import com.icsseseguridad.locationsecurity.events.OnListCompanySuccess;
import com.icsseseguridad.locationsecurity.events.OnListVisitorFailure;
import com.icsseseguridad.locationsecurity.events.OnListVisitorSuccess;
import com.icsseseguridad.locationsecurity.events.OnListVisitorVehicleFailure;
import com.icsseseguridad.locationsecurity.events.OnListVisitorVehicleSuccess;
import com.icsseseguridad.locationsecurity.events.OnRegisterVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisterVisitSuccess;
import com.icsseseguridad.locationsecurity.model.Clerk;
import com.icsseseguridad.locationsecurity.model.ControlVisit;
import com.icsseseguridad.locationsecurity.model.ListBanner;
import com.icsseseguridad.locationsecurity.model.ListClerk;
import com.icsseseguridad.locationsecurity.model.ListCompany;
import com.icsseseguridad.locationsecurity.model.ListVisit;
import com.icsseseguridad.locationsecurity.model.ListVisitor;
import com.icsseseguridad.locationsecurity.model.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.model.MultipleResource;
import com.icsseseguridad.locationsecurity.model.Visitor;
import com.icsseseguridad.locationsecurity.model.VisitorVehicle;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

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
