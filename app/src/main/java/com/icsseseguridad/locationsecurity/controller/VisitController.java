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

public class VisitController extends BaseController {

    public void getVisitors() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisitor> call = apiInterface.getVisitors(preferences.getToken());
        call.enqueue(new Callback<ListVisitor>() {
            @Override
            public void onFailure(Call<ListVisitor> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListVisitorFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListVisitor> call, Response<ListVisitor> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListVisitorFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListVisitorSuccess(response.body()));
                }
            }
        });
    }

    public void getVehicles() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisitorVehicle> call = apiInterface.getVisitorVehicles(preferences.getToken());
        call.enqueue(new Callback<ListVisitorVehicle>() {
            @Override
            public void onFailure(Call<ListVisitorVehicle> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListVisitorVehicleFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListVisitorVehicle> call, Response<ListVisitorVehicle> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListVisitorVehicleFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListVisitorVehicleSuccess(response.body()));
                }
            }
        });
    }

    public void getClerks() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListClerk> call = apiInterface.getClerks(preferences.getToken());
        call.enqueue(new Callback<ListClerk>() {
            @Override
            public void onFailure(Call<ListClerk> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListClerkFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListClerk> call, Response<ListClerk> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListClerkFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListClerkSuccess(response.body()));
                }
            }
        });
    }

    public void getCompanies() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListCompany> call = apiInterface.getCompanies(preferences.getToken());
        call.enqueue(new Callback<ListCompany>() {
            @Override
            public void onFailure(Call<ListCompany> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListCompanyFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListCompany> call, Response<ListCompany> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListCompanyFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListCompanySuccess(response.body()));
                }
            }
        });
    }

    public void saveVehicle(VisitorVehicle vehicle) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addVehicle(preferences.getToken(),
                vehicle.plate,
                vehicle.vehicle,
                vehicle.model,
                vehicle.type,
                vehicle.photo);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnAddVehicleFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnAddVehicleFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnAddVehicleFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnAddVehicleFailure(resource));
                } else {
                    VisitorVehicle vehicleSaved = gson.fromJson(gson.toJson(resource.result), VisitorVehicle.class);
                    EventBus.getDefault().postSticky(new OnAddVehicleSuccess(vehicleSaved));
                }
            }
        });
    }

    public void saveVisitor(Visitor visitor) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addVisitor(preferences.getToken(),
                visitor.dni,
                visitor.name,
                visitor.lastname,
                visitor.company,
                visitor.photo);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnAddVisitorFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnAddVisitorFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnAddVisitorFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnAddVisitorFailure(resource));
                } else {
                    Visitor visitorSaved = gson.fromJson(gson.toJson(resource.result), Visitor.class);
                    EventBus.getDefault().postSticky(new OnAddVisitorSuccess(visitorSaved));
                }
            }
        });
    }

    public void saveClerk(Clerk clerk) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addClerk(preferences.getToken(),
                clerk.dni,
                clerk.name,
                clerk.lastname,
                clerk.address);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnAddClerkFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnAddClerkFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnAddClerkFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnAddClerkFailure(resource));
                } else {
                    Clerk clerkSaved = gson.fromJson(gson.toJson(resource.result), Clerk.class);
                    EventBus.getDefault().postSticky(new OnAddClerkSuccess(clerkSaved));
                }
            }
        });
    }

    public void getActiveVisits() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisit> call = apiInterface.getActiveVisits(preferences.getToken());
        call.enqueue(new Callback<ListVisit>() {
            @Override
            public void onFailure(Call<ListVisit> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListActiveVisitFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListVisit> call, Response<ListVisit> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListActiveVisitFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListActiveVisitSuccess(response.body()));
                }
            }
        });
    }

    public void register(ControlVisit visit) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addVisit(preferences.getToken(),
                visit.vehicleId,
                visit.visitorId,
                visit.clerkId,
                visit.guardId,
                visit.persons,
                visit.materials,
                visit.latitude,
                visit.longitude,
                visit.image1,
                visit.image2,
                visit.image3,
                visit.image4,
                visit.image5);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnRegisterVisitFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnRegisterVisitFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnRegisterVisitFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnRegisterVisitFailure(resource));
                } else {
                    ControlVisit visitRegistered = gson.fromJson(gson.toJson(resource.result), ControlVisit.class);
                    EventBus.getDefault().postSticky(new OnRegisterVisitSuccess(visitRegistered));
                }
            }
        });
    }

    public void finish(Long visitId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.finishVisit(preferences.getToken(), visitId);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnFinishVisitFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnFinishVisitFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnFinishVisitFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnFinishVisitFailure(resource));
                } else {
                    EventBus.getDefault().postSticky(new OnFinishVisitSuccess(resource));
                }
            }
        });
    }

}