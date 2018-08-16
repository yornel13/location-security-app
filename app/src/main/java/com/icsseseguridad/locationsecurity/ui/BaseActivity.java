package com.icsseseguridad.locationsecurity.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.model.Watch;
import com.icsseseguridad.locationsecurity.service.LocationService;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.DefaultPreferences;

import org.greenrobot.eventbus.EventBus;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected ACProgressFlower.Builder builderDialog;
    protected ACProgressFlower dialog;

    protected SecurityApp app;

    protected AppPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (SecurityApp) getApplicationContext();
        preferences = new AppPreferences(this);
        builderDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Cargando")
                .textColor(Color.WHITE)
                .fadeColor(Color.DKGRAY);
        dialog = builderDialog.build();
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void setSupportActionBarBack(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
    }

    protected Location getLastKnowLocation() {
        if (SecurityApp.getAppContext() != null)
            return new DefaultPreferences(SecurityApp.getAppContext()).getLastKnownLoc();
        return null;
    }

    protected String getImeiAndSave(Watch watch) {
        String imei = getImei();
        DefaultPreferences defaultPreferences = new DefaultPreferences(SecurityApp.getAppContext());
        defaultPreferences.setImei(imei);
        defaultPreferences.setWatchId(watch.id);
        return imei;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    protected String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null)
            return telephonyManager.getDeviceId();
        else
            return android.os.Build.SERIAL;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public DefaultPreferences getDefaultPreference() {
        return new DefaultPreferences(SecurityApp.getAppContext());
    }
}
