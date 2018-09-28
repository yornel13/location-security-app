package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;

import java.util.List;

public class VehicleListViewModel extends AndroidViewModel {

    private static VehicleListViewModel mInstance = null;

    private final MediatorLiveData<List<VisitorVehicle>> mObservableVehicles;
    private AppDatabase appDataBase;

    public VehicleListViewModel(Application application) {
        super(application);
        mObservableVehicles = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<VisitorVehicle>> getVehicles() {
        return mObservableVehicles;
    }

    public void refreshObservables() {
        LiveData<List<VisitorVehicle>> vehicles = appDataBase.getVisitorVehicleDao().getAll();
        mObservableVehicles.addSource(vehicles, new Observer<List<VisitorVehicle>>() {
            @Override
            public void onChanged(@Nullable List<VisitorVehicle> vehicles) {
                mObservableVehicles.setValue(vehicles);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getVehicles() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
