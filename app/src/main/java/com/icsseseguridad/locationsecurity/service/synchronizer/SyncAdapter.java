package com.icsseseguridad.locationsecurity.service.synchronizer;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.service.entity.ListBanner;
import com.icsseseguridad.locationsecurity.service.entity.ListClerk;
import com.icsseseguridad.locationsecurity.service.entity.ListCompany;
import com.icsseseguridad.locationsecurity.service.entity.ListIncidence;
import com.icsseseguridad.locationsecurity.service.entity.ListReport;
import com.icsseseguridad.locationsecurity.service.entity.ListVehicleType;
import com.icsseseguridad.locationsecurity.service.entity.ListVisit;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitor;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;
import com.icsseseguridad.locationsecurity.service.repository.APIClient;
import com.icsseseguridad.locationsecurity.service.repository.APIInterface;
import com.icsseseguridad.locationsecurity.service.repository.PhotoController;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.viewmodel.BannerListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.BinnacleListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.ClerkListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.CompanyListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VehicleListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VisitListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VisitorListViewModel;

import java.io.File;
import java.net.URI;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SyncAdapter {

    private static final String TAG = "RepoIntentServiceAdapt";

    private AppDatabase db;
    private Context context;

    public SyncAdapter(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public AppPreferences getPreferences() {
        return new AppPreferences(context.getApplicationContext());
    }

    public void sync() {
        if (needSync()) {
            if (syncPhotos()) {
                syncReports();
                if (syncVehicles() && syncVisitors()) {
                    syncVisits();
                    if (!needSync()) {
                        updateDatabase();
                    }
                }
            }
        } else {
            updateDatabase();
        }
    }

    public boolean needSync() {
        List<Photo> photos = db.getPhotoDao().getAllUnsaved();
        List<VisitorVehicle> vehicles = db.getVisitorVehicleDao().getAllUnsaved();
        List<Visitor> visitors = db.getVisitorDao().getAllUnsaved();
        List<ControlVisit> visits = db.getControlVisitDao().getAllUnsaved();
        List<SpecialReport> reports = db.getSpecialReportDao().getAllUnsaved();
        if (photos.size() > 0
                || vehicles.size() > 0
                || visitors.size() > 0
                || visits.size() > 0
                || reports.size() > 0) {
            Log.d(TAG, "Database is needing sync");
            return true;
        }
        return false;
    }

    private void updateDatabase() {
        if (getPreferences().getGuard() == null) return;
        getPGSUpdateTime();
        if (getPreferences().getGuard() == null) return;
        getBanners();
        if (getPreferences().getGuard() == null) return;
        getVisitors();
        if (getPreferences().getGuard() == null) return;
        getVehicles();
        if (getPreferences().getGuard() == null) return;
        getVehiclesType();
        if (getPreferences().getGuard() == null) return;
        getClerks();
        if (getPreferences().getGuard() == null) return;
        getCompanies();
        if (getPreferences().getGuard() == null) return;
        getControlVisits();
        if (getPreferences().getGuard() == null) return;
        getIncidences();
        if (getPreferences().getGuard() == null) return;
        getSpecialReports();
    }

    private void getVisitors() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisitor> call = apiInterface.getVisitors(getPreferences().getToken());
        try {
            Response<ListVisitor> tasks = call.execute();
            ListVisitor data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                List<Visitor> visitorsToSave = db.getVisitorDao().getAllUnsaved();
                if (visitorsToSave.size() == 0) {
                    db.getVisitorDao().deleteAll();
                    db.getVisitorDao().insertAll(data.visitors);
                    Log.d(TAG, "Update visitors was successful");
                    VisitorListViewModel.refreshIfIsActive();
                } else {
                    Log.d(TAG, "Can't update visitors, this need sync first");
                }
            } else {
                Log.e(TAG, "Error in data visitors, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating visitors");
            e.printStackTrace();
        }
    }

    private void getVehicles() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisitorVehicle> call = apiInterface.getVisitorVehicles(getPreferences().getToken());
        try {
            Response<ListVisitorVehicle> tasks = call.execute();
            ListVisitorVehicle data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                List<VisitorVehicle> vehiclesToSave = db.getVisitorVehicleDao().getAllUnsaved();
                if (vehiclesToSave.size() == 0) {
                    db.getVisitorVehicleDao().deleteAll();
                    db.getVisitorVehicleDao().insertAll(data.vehicles);
                    Log.d(TAG, "Update visitors vehicles was successful");
                    VehicleListViewModel.refreshIfIsActive();
                } else {
                    Log.d(TAG, "Can't update visitors vehicles, this need sync first");
                }
            } else {
                Log.e(TAG, "Error in data vehicles, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating vehicles");
            e.printStackTrace();
        }
    }

    private void getVehiclesType() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVehicleType> call = apiInterface.getVehiclesTypes(getPreferences().getToken());
        try {
            Response<ListVehicleType> tasks = call.execute();
            ListVehicleType data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                db.getVehicleTypeDao().deleteAll();
                db.getVehicleTypeDao().insertAll(data.types);
                Log.d(TAG, "Update vehicles types was successful");
                ClerkListViewModel.refreshIfIsActive();
            } else {
                Log.e(TAG, "Error in data vehicles types, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating vehicles types");
            e.printStackTrace();
        }
    }

    private void getClerks() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListClerk> call = apiInterface.getClerks(getPreferences().getToken());
        try {
            Response<ListClerk> tasks = call.execute();
            ListClerk data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                db.getClerkDao().deleteAll();
                db.getClerkDao().insertAll(data.clerks);
                Log.d(TAG, "Update clerks was successful");
                ClerkListViewModel.refreshIfIsActive();
            } else {
                Log.e(TAG, "Error in data clerks, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating clerks");
            e.printStackTrace();
        }
    }

    private void getCompanies() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListCompany> call = apiInterface.getCompanies(getPreferences().getToken());
        try {
            Response<ListCompany> tasks = call.execute();
            final ListCompany data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                db.getCompanyDao().deleteAll();
                db.getCompanyDao().insertAll(data.companies);
                Log.d(TAG, "Update companies was successful");
                CompanyListViewModel.refreshIfIsActive();
            } else {
                Log.e(TAG, "Error in data companies, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating companies");
            e.printStackTrace();
        }
    }

    private void getControlVisits() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListVisit> call = apiInterface.getActiveVisits(getPreferences().getToken());
        try {
            Response<ListVisit> tasks = call.execute();
            final ListVisit data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                List<ControlVisit> visitsToSave = db.getControlVisitDao().getAllUnsaved();
                if (visitsToSave.size() == 0) {
                    db.getControlVisitDao().deleteAll();
                    db.getControlVisitDao().insertAll(data.visits);
                    Log.d(TAG, "Update visits was successful");
                    VisitListViewModel.refreshIfIsActive();
                } else {
                    Log.d(TAG, "Can't update visits, this need sync first");
                }
            } else {
                Log.e(TAG, "Error in data visits, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating visits");
            e.printStackTrace();
        }
    }

    private void getBanners() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListBanner> call = apiInterface.getBanners(getPreferences().getToken());
        try {
            Response<ListBanner> tasks = call.execute();
            final ListBanner data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                db.getBannerDao().deleteAll();
                db.getBannerDao().insertAll(data.banners);
                BannerListViewModel.refreshIfIsActive();
                Log.d(TAG, "Update banners was successful");
            } else {
                Log.e(TAG, "Error in data banners, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating banners");
            e.printStackTrace();
        }
    }

    private void getIncidences() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListIncidence> call = apiInterface.getIncidences(getPreferences().getToken());
        try {
            Response<ListIncidence> tasks = call.execute();
            final ListIncidence data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                db.getIncidenceDao().deleteAll();
                db.getIncidenceDao().insertAll(data.incidences);
                Log.d(TAG, "Update incidences was successful");
            } else {
                Log.e(TAG, "Error in data incidences, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating incidences");
            e.printStackTrace();
        }
    }

    private void getSpecialReports() {
        if (getPreferences().getGuard() == null) {
            Log.e(TAG, "No guard logger");
            return;
        }
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListReport> call = apiInterface.getGuardReports(getPreferences().getToken(), getPreferences().getGuard().id);
        try {
            Response<ListReport> tasks = call.execute();
            final ListReport data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                List<SpecialReport> reportsToSave = db.getSpecialReportDao().getAllUnsaved();
                if (reportsToSave.size() == 0) {
                    db.getSpecialReportDao().deleteAll();
                    db.getSpecialReportDao().insertAll(data.reports);
                    Log.d(TAG, "Update reports was successful");
                    BinnacleListViewModel.refreshIfIsActive();
                } else {
                    Log.d(TAG, "Can't update reports, this need sync first");
                }
            } else {
                Log.e(TAG, "Error in data reports, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating reports");
            e.printStackTrace();
        }
    }

    private void getPGSUpdateTime() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ConfigUtility> call = apiInterface.getUpdateGPS(getPreferences().getToken());
        try {
            Response<ConfigUtility> tasks = call.execute();
            ConfigUtility data =  tasks.body();
            if (tasks.isSuccessful() && data != null) {
                getPreferences().setGpsUpdate(data);
                Log.d(TAG, "Update gps time is: " + data.value);
                Log.d(TAG, "Update gps time was successful");
            } else {
                Log.e(TAG, "Error in data gps time, code: " + tasks.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating gps time");
            e.printStackTrace();
        }
    }

    /**
     * This method will save all newly photos in firebase and updated url in database.
     * @return Boolean, returns true if everything has been saved successfully
     */
    private boolean syncPhotos() {
        List<Photo> photos = db.getPhotoDao().getAllUnsaved();
        for (Photo photo: photos) {
            File file = new File(URI.create(photo.uri).getPath());
            if (file.exists()) {
                String url = new PhotoController().saveSynchronously(photo.uri);
                if (url == null) {
                    Log.e(TAG, "Error uploading photo");
                    continue;
                } else {
                    Log.d(TAG, "-> Uploaded photo success <-");
                }
                switch (photo.linkedType) {
                    case VEHICLE:
                        VisitorVehicle vehicle = db.getVisitorVehicleDao().findById(photo.linkedId);
                        if (vehicle != null) {
                            vehicle.photo = url;
                            db.getVisitorVehicleDao().update(vehicle);
                        }
                        break;
                    case VISITOR:
                        Visitor visitor = db.getVisitorDao().findById(photo.linkedId);
                        if (visitor != null) {
                            visitor.photo = url;
                            db.getVisitorDao().update(visitor);
                        }
                        break;
                    case MATERIAL:
                        ControlVisit visit = db.getControlVisitDao().findById(photo.linkedId);
                        if (visit != null) {
                            if (visit.image1 == null) {
                                visit.image1 = url;
                            } else if (visit.image2 == null) {
                                visit.image2 = url;
                            } else if (visit.image3 == null) {
                                visit.image3 = url;
                            } else if (visit.image4 == null) {
                                visit.image4 = url;
                            } else if (visit.image5 == null) {
                                visit.image5 = url;
                            }
                            db.getControlVisitDao().update(visit);
                        }
                        break;
                    case REPORT:
                        SpecialReport report = db.getSpecialReportDao().findById(photo.linkedId);
                        if (report != null) {
                            if (report.image1 == null) {
                                report.image1 = url;
                            } else if (report.image2 == null) {
                                report.image2 = url;
                            } else if (report.image3 == null) {
                                report.image3 = url;
                            } else if (report.image4 == null) {
                                report.image4 = url;
                            } else if (report.image5 == null) {
                                report.image5 = url;
                            }
                            db.getSpecialReportDao().update(report);
                        }
                        break;
                }
            } else {
                Log.e(TAG, "Photo was deleted by user");
            }
            db.getPhotoDao().delete(photo);
        }
        return db.getPhotoDao().getAllUnsaved().size() <= 0;
    }

    /**
     * This method will save all newly created vehicles.
     * @return Boolean, returns false if some error has occurred.
     */
    private boolean syncVehicles() {
        List<VisitorVehicle> vehicles = db.getVisitorVehicleDao().getAllUnsaved();
        boolean isSuccess = true;
        for (VisitorVehicle vehicle: vehicles) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MultipleResource> call = apiInterface.syncVehicle(getPreferences().getToken(),
                    vehicle);
            try {
                Response<MultipleResource> tasks = call.execute();
                final MultipleResource data =  tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    VisitorVehicle vehicleResponse = gson()
                            .fromJson(gson().toJson(data.result), VisitorVehicle.class);
                    db.getControlVisitDao().updateVehicleId(vehicle.id, vehicleResponse.id);
                    db.getVisitorVehicleDao().delete(vehicle);
                    for (ControlVisit visit : db.getControlVisitDao()
                            .findAllByVehicleId(vehicleResponse.id)) {
                        visit.vehicle = vehicleResponse;
                        db.getControlVisitDao().update(visit);
                    }
                    Log.d(TAG, "-> Success sync vehicle with plate: " + vehicle.plate + " <-");
                } else {
                    isSuccess = false;
                    Log.e(TAG, "Error sync vehicle with plate: " + vehicle.plate);
                }
            } catch (Exception e) {
                isSuccess = false;
                Log.e(TAG, "Error sync vehicle");
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * This method will save all newly created visitors.
     * @return Boolean, returns false if some error has occurred.
     */
    private boolean syncVisitors() {
        List<Visitor> visitors = db.getVisitorDao().getAllUnsaved();
        boolean isSuccess = true;
        for (Visitor visitor: visitors) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MultipleResource> call = apiInterface.syncVisitor(getPreferences().getToken(),
                    visitor);
            try {
                Response<MultipleResource> tasks = call.execute();
                final MultipleResource data =  tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    Visitor visitorResponse = gson()
                            .fromJson(gson().toJson(data.result), Visitor.class);
                    db.getControlVisitDao().updateVisitorId(visitor.id, visitorResponse.id);
                    db.getVisitorDao().delete(visitor);
                    for (ControlVisit visit : db.getControlVisitDao()
                            .findAllByVisitorId(visitorResponse.id)) {
                        visit.visitor = visitorResponse;
                        db.getControlVisitDao().update(visit);
                    }
                    Log.d(TAG, "-> Success sync visitor with dni: " + visitorResponse.dni + " <-");
                } else {
                    isSuccess = false;
                    Log.e(TAG, "Error sync visitor with dni: " + visitor.dni);
                }
            } catch (Exception e) {
                isSuccess = false;
                Log.e(TAG, "Error sync visitor");
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * This method will save all newly created visits.
     * @return Boolean, return false if some error has occurred.
     */
    private boolean syncVisits() {
        List<ControlVisit> visits = db.getControlVisitDao().getAllUnsaved();
        boolean isSuccess = true;
        for (ControlVisit visit: visits) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MultipleResource> call = apiInterface.syncVisit(getPreferences().getToken(),
                    visit);
            try {
                Response<MultipleResource> tasks = call.execute();
                final MultipleResource data =  tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    ControlVisit visitResponse = gson()
                            .fromJson(gson().toJson(data.result), ControlVisit.class);
                    visit.sync = true;
                    db.getControlVisitDao().update(visit);
                    Log.d(TAG, "-> Success sync visit with id: " + visitResponse.id + " <-");
                } else {
                    isSuccess = false;
                    Log.e(TAG, "Error sync visit with id: " + visit.id);
                }
            } catch (Exception e) {
                isSuccess = false;
                Log.e(TAG, "Error sync visit");
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * This method will save all newly created visits.
     * @return Boolean, return false if some error has occurred.
     */
    private boolean syncReports() {
        List<SpecialReport> reports = db.getSpecialReportDao().getAllUnsaved();
        boolean isSuccess = true;
        for (SpecialReport report: reports) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MultipleResource> call = apiInterface.syncReport(getPreferences().getToken(), report);
            try {
                Response<MultipleResource> tasks = call.execute();
                final MultipleResource data =  tasks.body();
                if (tasks.isSuccessful() && data != null) {
                    SpecialReport reportResponse = gson()
                            .fromJson(gson().toJson(data.result), SpecialReport.class);
                    report.sync = true;
                    db.getSpecialReportDao().update(report);
                    Log.d(TAG, "-> Success sync report: " + reportResponse.id + " <-");
                } else {
                    isSuccess = false;
                    Log.e(TAG, "Error sync report with id: " + report.id);
                }
            } catch (Exception e) {
                isSuccess = false;
                Log.e(TAG, "Error sync report");
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
