package com.icsseseguridad.locationsecurity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.icsseseguridad.locationsecurity.service.background.AppLocationService;
import com.icsseseguridad.locationsecurity.service.background.LocationService;
import com.icsseseguridad.locationsecurity.service.background.TrackingService;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.Clerk;
import com.icsseseguridad.locationsecurity.service.entity.Company;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.service.entity.ListChatWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListClerk;
import com.icsseseguridad.locationsecurity.service.entity.ListCompany;
import com.icsseseguridad.locationsecurity.service.entity.ListIncidence;
import com.icsseseguridad.locationsecurity.service.entity.ListRepliesWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitor;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;
import com.icsseseguridad.locationsecurity.service.event.OnGetUpdateGpsFailure;
import com.icsseseguridad.locationsecurity.service.event.OnGetUpdateGpsSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListClerkFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListClerkSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListCompanyFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListCompanySuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListIncidenceFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListIncidenceSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadMessagesFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadMessagesSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadRepliesFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadRepliesSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListVisitorFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListVisitorSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListVisitorVehicleFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListVisitorVehicleSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncClerks;
import com.icsseseguridad.locationsecurity.service.event.OnSyncIncidences;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.service.event.OnSyncVehicles;
import com.icsseseguridad.locationsecurity.service.event.OnSyncVisitors;
import com.icsseseguridad.locationsecurity.service.repository.BinnacleController;
import com.icsseseguridad.locationsecurity.service.repository.TabletPositionController;
import com.icsseseguridad.locationsecurity.service.repository.VisitController;
import com.icsseseguridad.locationsecurity.service.synchronizer.AlertSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.SavePositionJob;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;


public class SecurityApp extends Application {

    public static final String TAG = "SecurityApp";

    private static Context context;
    private AppPreferences preferences;

    public static Context getAppContext() {
        return SecurityApp.context;
    }

    public ListVisitor visitors;
    public ListVisitorVehicle vehicles;
    public ListClerk clerks;
    public ListCompany companies;
    public ListIncidence incidences;

    public VisitorVehicle vehicle;
    public Visitor visitor;
    public Clerk clerk;
    public Company company;
    private ControlVisit newVisit;
    public ControlVisit visit;
    public SpecialReport report;
    public Chat chat;
    public ListChatWithUnread unreadMessages;
    public ListRepliesWithUnread unreadReplies;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Fresco.initialize(this);
        SecurityApp.context = getApplicationContext();
        preferences = new AppPreferences(this);

        // Running alert job service exists any alert
        AlertSyncJob.jobScheduler(this);
    }

    public void getDefaultData(boolean bVehicles, boolean bVisitors, boolean bClerks, boolean bCompanies) {
        VisitController visitController = new VisitController();
        if (visitors == null && bVisitors) {
            visitController.getVisitors();
        }
        if (vehicles == null && bVehicles) {
            visitController.getVehicles();
        }
        if (clerks == null && bClerks) {
            visitController.getClerks();
        }
        if (companies == null && bCompanies) {
            visitController.getCompanies();
        }
    }

    public void getDefaultIncidences(boolean update) {
        if (incidences == null || update) {
            new BinnacleController().getIncidences();
        }
    }

    public void getDefaultData() {
        VisitController visitController = new VisitController();
        visitController.getVisitors();
        visitController.getVehicles();
        visitController.getClerks();
        visitController.getCompanies();

        TabletPositionController positionController = new TabletPositionController();
        positionController.getUpdateGPS();
    }

    public Boolean isReady() {
        if (visitors != null
                && vehicles != null
                && clerks != null
                && companies != null) {
            return true;
        } else {
            return false;
        }
    }

    public ListVisitor getVisitors() {
        return visitors;
    }

    public ListVisitorVehicle getVehicles() {
        return vehicles;
    }

    public ListClerk getClerks() {
        return clerks;
    }

    public ListCompany getCompanies() {
        return companies;
    }

    public ControlVisit getNewVisit() {
        return newVisit;
    }

    public void setNewVisit(ControlVisit newVisit) {
        this.newVisit = newVisit;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListVisitorSuccess(OnListVisitorSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListVisitorSuccess.class);
        this.visitors = event.list;
        EventBus.getDefault().post(new OnSyncVisitors(true));

        // db.getVisitorDao().insertAll(event.list.visitors);
        // Log.e("Sync", "size; " + db.getVisitorDao().getAll().size());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListVisitorFailure(OnListVisitorFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListVisitorFailure.class);
        System.err.println(event.message);
        EventBus.getDefault().post(new OnSyncVisitors(false));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListVisitorVehicleSuccess(OnListVisitorVehicleSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListVisitorVehicleSuccess.class);
        this.vehicles = event.list;
        EventBus.getDefault().post(new OnSyncVehicles(true));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListVisitorVehicleFailure(OnListVisitorVehicleFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListVisitorVehicleFailure.class);
        System.err.println(event.message);
        EventBus.getDefault().post(new OnSyncVehicles(false));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListClerkSuccess(OnListClerkSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListClerkSuccess.class);
        this.clerks = event.list;
        EventBus.getDefault().post(new OnSyncVisitors(true));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListClerkFailure(OnListClerkFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListClerkFailure.class);
        System.err.println(event.message);
        EventBus.getDefault().post(new OnSyncClerks(false));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListCompanySuccess(OnListCompanySuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListCompanySuccess.class);
        this.companies = event.list;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListCompanyFailure(OnListCompanyFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListCompanyFailure.class);
        System.err.println(event.message);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListIncidenceSuccess(OnListIncidenceSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListIncidenceSuccess.class);
        this.incidences = event.list;
        EventBus.getDefault().postSticky(new OnSyncIncidences(true));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListIncidenceFailure(OnListIncidenceFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListIncidenceFailure.class);
        System.err.println(event.message);
        EventBus.getDefault().postSticky(new OnSyncIncidences(false));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetUpdateGpsSuccess(OnGetUpdateGpsSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnGetUpdateGpsSuccess.class);
        new AppPreferences(getApplicationContext()).setGpsUpdate(event.utility);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetUpdateGpsFailure(OnGetUpdateGpsFailure event) {
        EventBus.getDefault().removeStickyEvent(OnGetUpdateGpsFailure.class);
        Log.e("Application", "Update GPS error: "+event.message);
    }

    public VisitorVehicle getVehicle() {
        return vehicle;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public Clerk getClerk() {
        return clerk;
    }

    public Company getCompany() {
        return company;
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListUnreadMessagesSuccess(OnListUnreadMessagesSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListUnreadMessagesSuccess.class);
        this.unreadMessages = event.list;
        Log.i("SecurityApp", this.unreadMessages.unread + " unread messages");
        EventBus.getDefault().post(new OnSyncUnreadMessages(this.unreadMessages));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListUnreadMessagesFailure(OnListUnreadMessagesFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListUnreadMessagesFailure.class);
        Log.e("SecurityApp", "Failed to get unread messages");
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListUnreadRepliesSuccess(OnListUnreadRepliesSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListUnreadRepliesSuccess.class);
        this.unreadReplies = event.list;
        Log.i("SecurityApp", this.unreadReplies.unread + " unread replies");
        EventBus.getDefault().post(new OnSyncUnreadReplies(this.unreadReplies));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListUnreadRepliesFailure(OnListUnreadRepliesFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListUnreadRepliesFailure.class);
        Log.e("SecurityApp", "Failed to get unread replies");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventBus.getDefault().unregister(this);
    }
}
