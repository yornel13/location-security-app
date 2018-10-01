package com.icsseseguridad.locationsecurity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Banner;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;

import java.util.List;

public class BannerListViewModel extends AndroidViewModel {

    private static BannerListViewModel mInstance = null;

    private final MediatorLiveData<List<Banner>> mObservableBanners;
    private AppDatabase appDataBase;

    public BannerListViewModel(Application application) {
        super(application);
        mObservableBanners = new MediatorLiveData<>();
        appDataBase = AppDatabase.getInstance(application.getApplicationContext());
        refreshObservables();
        mInstance = this;
    }

    public MutableLiveData<List<Banner>> getBanners() {
        return mObservableBanners;
    }

    public void refreshObservables() {
        LiveData<List<Banner>> banners = appDataBase.getBannerDao().getAll();
        mObservableBanners.addSource(banners, new Observer<List<Banner>>() {
            @Override
            public void onChanged(@Nullable List<Banner> banners) {
                mObservableBanners.setValue(banners);
            }
        });
    }

    public static void refreshIfIsActive() {
        if (mInstance != null && mInstance.getBanners() != null && mInstance.appDataBase != null) {
            mInstance.refreshObservables();
        }
    }
}
