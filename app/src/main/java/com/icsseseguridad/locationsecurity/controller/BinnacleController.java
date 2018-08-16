package com.icsseseguridad.locationsecurity.controller;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesFailure;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesSuccess;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportFailure;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportSuccess;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceFailure;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceSuccess;
import com.icsseseguridad.locationsecurity.events.OnPostReplyFailure;
import com.icsseseguridad.locationsecurity.events.OnPostReplySuccess;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportSuccess;
import com.icsseseguridad.locationsecurity.model.ListIncidence;
import com.icsseseguridad.locationsecurity.model.ListReply;
import com.icsseseguridad.locationsecurity.model.ListReport;
import com.icsseseguridad.locationsecurity.model.MultipleResource;
import com.icsseseguridad.locationsecurity.model.Reply;
import com.icsseseguridad.locationsecurity.model.SpecialReport;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BinnacleController extends BaseController {

    public void getIncidences() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListIncidence> call = apiInterface.getIncidences(preferences.getToken());
        call.enqueue(new Callback<ListIncidence>() {
            @Override
            public void onFailure(Call<ListIncidence> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListIncidenceFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListIncidence> call, Response<ListIncidence> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListIncidenceFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListIncidenceSuccess(response.body()));
                }
            }
        });
    }

    public void getGuardReports(Long guardId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListReport> call = apiInterface.getGuardReports(preferences.getToken(), guardId);
        call.enqueue(new Callback<ListReport>() {
            @Override
            public void onFailure(Call<ListReport> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListGuardReportFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListReport> call, Response<ListReport> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListGuardReportFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListGuardReportSuccess(response.body()));
                }
            }
        });
    }

    public void register(SpecialReport report) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addReport(preferences.getToken(),
                report.watchId,
                report.incidenceId,
                report.title,
                report.observation,
                report.latitude,
                report.longitude,
                report.image1,
                report.image2,
                report.image3,
                report.image4,
                report.image5);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnRegisterReportFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnRegisterReportFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnRegisterReportFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnRegisterReportFailure(resource));
                } else {
                    SpecialReport specialReport = gson.fromJson(gson.toJson(resource.result), SpecialReport.class);
                    EventBus.getDefault().postSticky(new OnRegisterReportSuccess(specialReport));
                }
            }
        });
    }

    public void getReplies(Long reportId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListReply> call = apiInterface.getReplies(preferences.getToken(), reportId);
        call.enqueue(new Callback<ListReply>() {
            @Override
            public void onFailure(Call<ListReply> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnGetRepliesFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListReply> call, Response<ListReply> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnGetRepliesFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnGetRepliesSuccess(response.body()));
                }
            }
        });
    }

    public void postReply(Reply reply) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.postReply(preferences.getToken(),
                reply.reportId,
                reply.guardId,
                reply.userName,
                reply.text);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnPostReplyFailure(
                        new MultipleResource(false, SecurityApp
                                .getAppContext().getString(R.string.error_connection))
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnPostReplyFailure(resource));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnPostReplyFailure(
                                new MultipleResource(false, SecurityApp
                                        .getAppContext().getString(R.string.error_connection))
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnPostReplyFailure(resource));
                } else {
                    Reply postReply = gson.fromJson(gson.toJson(resource.result), Reply.class);
                    EventBus.getDefault().postSticky(new OnPostReplySuccess(postReply));
                }
            }
        });
    }
}
