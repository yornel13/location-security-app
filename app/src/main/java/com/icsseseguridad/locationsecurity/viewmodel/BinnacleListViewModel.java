package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import java.util.List;

public class BinnacleListViewModel extends AndroidViewModel {

    private static BinnacleListViewModel mInstance = null;

    private final MediatorLiveData<List<SpecialReport>> mObservableReports;
    private AppDatabase appDataBase;

    public BinnacleListViewModel(Application application) {
        super(application);
        mObservableReports = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<SpecialReport>> getVisits() {
        return mObservableReports;
    }

    public void refreshObservables() {
        AppPreferences preferences = new AppPreferences(getApplication());
        if (preferences.getGuard() == null) return;
        LiveData<List<SpecialReport>> visits = appDataBase.getSpecialReportDao()
                .findAllByGuardId(preferences.getGuard().id);
        mObservableReports.addSource(visits, new Observer<List<SpecialReport>>() {
            @Override
            public void onChanged(@Nullable List<SpecialReport> visits) {
                mObservableReports.setValue(visits);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getVisits() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
