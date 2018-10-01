package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Company;
import com.icsseseguridad.locationsecurity.service.entity.VehicleType;

import java.util.List;

public class VehicleTypeListViewModel extends AndroidViewModel {

    private static VehicleTypeListViewModel mInstance = null;

    private final MediatorLiveData<List<VehicleType>> mObservableVehiclestypes;
    private AppDatabase appDataBase;

    public VehicleTypeListViewModel(Application application) {
        super(application);
        mObservableVehiclestypes = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<VehicleType>> getVehicles() {
        return mObservableVehiclestypes;
    }

    public void refreshObservables() {
        LiveData<List<VehicleType>> vehicles = appDataBase.getVehicleTypeDao().getAll();
        mObservableVehiclestypes.addSource(vehicles, new Observer<List<VehicleType>>() {
            @Override
            public void onChanged(@Nullable List<VehicleType> vehicles) {
                mObservableVehiclestypes.setValue(vehicles);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getVehicles() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
