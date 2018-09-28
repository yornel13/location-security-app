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

import java.util.List;

public class CompanyListViewModel extends AndroidViewModel {

    private static CompanyListViewModel mInstance = null;

    private final MediatorLiveData<List<Company>> mObservableCompanies;
    private AppDatabase appDataBase;

    public CompanyListViewModel(Application application) {
        super(application);
        mObservableCompanies = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<Company>> getCompanies() {
        return mObservableCompanies;
    }

    public void refreshObservables() {
        LiveData<List<Company>> companies = appDataBase.getCompanyDao().getAll();
        mObservableCompanies.addSource(companies, new Observer<List<Company>>() {
            @Override
            public void onChanged(@Nullable List<Company> companies) {
                mObservableCompanies.setValue(companies);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getCompanies() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
