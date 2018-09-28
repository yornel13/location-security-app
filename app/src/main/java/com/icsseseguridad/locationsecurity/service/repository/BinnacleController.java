package com.icsseseguridad.locationsecurity.service.repository;

import android.util.Log;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnGetRepliesFailure;
import com.icsseseguridad.locationsecurity.service.event.OnGetRepliesSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardReportFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardReportSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListIncidenceFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListIncidenceSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadRepliesFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadRepliesSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnPostReplyFailure;
import com.icsseseguridad.locationsecurity.service.event.OnPostReplySuccess;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterReportFailure;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterReportSuccess;
import com.icsseseguridad.locationsecurity.service.entity.ListIncidence;
import com.icsseseguridad.locationsecurity.service.entity.ListRepliesWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListReply;
import com.icsseseguridad.locationsecurity.service.entity.ListReport;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.Reply;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;

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

    public void getUnreadReports() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListRepliesWithUnread> call = apiInterface.getUnreadReplies(preferences.getToken(), preferences.getGuard().id);
        call.enqueue(new Callback<ListRepliesWithUnread>() {
            @Override
            public void onFailure(Call<ListRepliesWithUnread> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListUnreadRepliesFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListRepliesWithUnread> call, Response<ListRepliesWithUnread> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListUnreadRepliesFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListUnreadRepliesSuccess(response.body()));
                }
            }
        });
    }

    public void putReportRead(final Long reportId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.putReportRead(preferences.getToken(), reportId);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                Log.e("Controller", "Failure Make replies read for report id = " + reportId);
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    Log.e("Controller", "Failure Make replies read for report id = " + reportId);
                    return;
                }
                Log.e("Controller", "Success Make replies read for report id = " + reportId);
                getUnreadReports();
            }
        });
    }
}
