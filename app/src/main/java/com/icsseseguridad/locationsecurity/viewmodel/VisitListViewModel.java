package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;

import java.util.List;

public class VisitListViewModel extends AndroidViewModel {

    private static VisitListViewModel mInstance = null;

    private final MediatorLiveData<List<ControlVisit>> mObservableVisits;
    private AppDatabase appDataBase;

    public VisitListViewModel(Application application) {
        super(application);
        mObservableVisits = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<ControlVisit>> getVisits() {
        return mObservableVisits;
    }

    public void refreshObservables() {
        LiveData<List<ControlVisit>> visits = appDataBase.getControlVisitDao().getAll();
        mObservableVisits.addSource(visits, new Observer<List<ControlVisit>>() {
            @Override
            public void onChanged(@Nullable List<ControlVisit> visits) {
                mObservableVisits.setValue(visits);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getVisits() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
