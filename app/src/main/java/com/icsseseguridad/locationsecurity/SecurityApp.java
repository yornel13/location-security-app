package com.icsseseguridad.locationsecurity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.icsseseguridad.locationsecurity.controller.BinnacleController;
import com.icsseseguridad.locationsecurity.controller.TabletPositionController;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnGetUpdateGpsFailure;
import com.icsseseguridad.locationsecurity.events.OnGetUpdateGpsSuccess;
import com.icsseseguridad.locationsecurity.events.OnListClerkFailure;
import com.icsseseguridad.locationsecurity.events.OnListClerkSuccess;
import com.icsseseguridad.locationsecurity.events.OnListCompanyFailure;
import com.icsseseguridad.locationsecurity.events.OnListCompanySuccess;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceFailure;
import com.icsseseguridad.locationsecurity.events.OnListIncidenceSuccess;
import com.icsseseguridad.locationsecurity.events.OnListVisitorFailure;
import com.icsseseguridad.locationsecurity.events.OnListVisitorSuccess;
import com.icsseseguridad.locationsecurity.events.OnListVisitorVehicleFailure;
import com.icsseseguridad.locationsecurity.events.OnListVisitorVehicleSuccess;
import com.icsseseguridad.locationsecurity.events.OnSyncClerks;
import com.icsseseguridad.locationsecurity.events.OnSyncIncidences;
import com.icsseseguridad.locationsecurity.events.OnSyncVehicles;
import com.icsseseguridad.locationsecurity.events.OnSyncVisitors;
import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.Clerk;
import com.icsseseguridad.locationsecurity.model.Company;
import com.icsseseguridad.locationsecurity.model.ControlVisit;
import com.icsseseguridad.locationsecurity.model.ListClerk;
import com.icsseseguridad.locationsecurity.model.ListCompany;
import com.icsseseguridad.locationsecurity.model.ListIncidence;
import com.icsseseguridad.locationsecurity.model.ListVisitor;
import com.icsseseguridad.locationsecurity.model.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.model.Visitor;
import com.icsseseguridad.locationsecurity.model.VisitorVehicle;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecurityApp extends Application {

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

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Fresco.initialize(this);
        SecurityApp.context = getApplicationContext();
        preferences = new AppPreferences(this);
        getDefaultData();
        getDefaultIncidences(false);
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventBus.getDefault().unregister(this);
    }
}
