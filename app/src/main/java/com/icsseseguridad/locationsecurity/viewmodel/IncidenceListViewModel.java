package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;

import java.util.List;

public class IncidenceListViewModel extends AndroidViewModel {

    private static IncidenceListViewModel mInstance = null;

    private final MediatorLiveData<List<Incidence>> mObservableIncidences;
    private AppDatabase appDataBase;

    public IncidenceListViewModel(Application application) {
        super(application);
        mObservableIncidences = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<Incidence>> getIncidences() {
        return mObservableIncidences;
    }

    public void refreshObservables() {
        LiveData<List<Incidence>> incidences = appDataBase.getIncidenceDao().getAll();
        mObservableIncidences.addSource(incidences, new Observer<List<Incidence>>() {
            @Override
            public void onChanged(@Nullable List<Incidence> incidences) {
                mObservableIncidences.setValue(incidences);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getIncidences() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
