package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;

import java.util.List;

public class VisitorListViewModel extends AndroidViewModel {

    private static VisitorListViewModel mInstance = null;

    private final MediatorLiveData<List<Visitor>> mObservableVisitors;
    private AppDatabase appDataBase;

    public VisitorListViewModel(Application application) {
        super(application);
        mObservableVisitors = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<Visitor>> getVisitors() {
        return mObservableVisitors;
    }

    public void refreshObservables() {
        LiveData<List<Visitor>> visitors = appDataBase.getVisitorDao().getAll();
        mObservableVisitors.addSource(visitors, new Observer<List<Visitor>>() {
            @Override
            public void onChanged(@Nullable List<Visitor> visitors) {
                mObservableVisitors.setValue(visitors);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getVisitors() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
