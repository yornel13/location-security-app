package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Clerk;

import java.util.List;

public class ClerkListViewModel extends AndroidViewModel {

    private static ClerkListViewModel mInstance = null;

    private final MediatorLiveData<List<Clerk>> mObservableClerks;
    private AppDatabase appDataBase;

    public ClerkListViewModel(Application application) {
        super(application);
        mObservableClerks = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<Clerk>> getClerks() {
        return mObservableClerks;
    }

    public void refreshObservables() {
        LiveData<List<Clerk>> clerks = appDataBase.getClerkDao().getAll();
        mObservableClerks.addSource(clerks, new Observer<List<Clerk>>() {
            @Override
            public void onChanged(@Nullable List<Clerk> clerks) {
                mObservableClerks.setValue(clerks);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getClerks() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
