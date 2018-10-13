package com.icsseseguridad.locationsecurity.view.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.background.TrackingService;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.event.OnUserUnauthorized;
import com.icsseseguridad.locationsecurity.service.synchronizer.AlertSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.SendAlert;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected ACProgressFlower.Builder builderDialog;
    protected ACProgressFlower dialog;

    protected SecurityApp app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (SecurityApp) getApplicationContext();
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
            return new AppPreferences(getApplicationContext()).getLastKnownLoc();
        return null;
    }

    protected String getImeiAndSave() {
        String imei = getImei();
        getPreferences().setImei(imei);
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

    public AppPreferences getPreferences() {
        return new AppPreferences(getApplicationContext());
    }


    private AlertDialog dialogSOS;
    private Button cancelButton;
    private TextView countSend;
    private TextView countTitle;
    private int i;
    public void dialogSOS() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);
        dialogBuilder.setView(dialogView);
        i = 0;
        countSend = dialogView.findViewById(R.id.count);
        countTitle = dialogView.findViewById(R.id.title);
        final ProgressBar progressBar = dialogView.findViewById(R.id.progress);
        cancelButton = dialogView.findViewById(R.id.button);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        countSend.setText("5");
        progressBar.setVisibility(View.VISIBLE);
        final CountDownTimer mCountDownTimer = new CountDownTimer(6000,10) {

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                progressBar.setProgress(i *100/(5000/10));
                if (i == 100)
                    countSend.setText(String.valueOf(4));
                if (i == 200)
                    countSend.setText(String.valueOf(3));
                if (i == 300)
                    countSend.setText(String.valueOf(2));
                if (i == 400)
                    countSend.setText(String.valueOf(1));
                if (i == 500)
                    countSend.setText("...");
            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                countTitle.setText("Enviando Alerta");
                countSend.setText("...");
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                cancelButton.setEnabled(false);
                sendAlert();
            }
        };
        dialogSOS = dialogBuilder.create();
        dialogSOS.setCanceledOnTouchOutside(false);
        dialogSOS.setCancelable(false);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountDownTimer.cancel();
                dialogSOS.dismiss();
            }
        });
        mCountDownTimer.start();
        dialogSOS.show();
    }

    public void sendAlert() {
        final Alert alert = new Alert();
        alert.cause = Alert.CAUSE.SOS1;
        alert.type = Alert.CAUSE.SOS1;
        final AppPreferences preferences = getPreferences();
        alert.message = "Alerta activada por el guardia "+preferences.getGuard().getFullname();
        alert.guardId = preferences.getGuard().id;
        //alert.latitude = String.valueOf(preferences.getLastKnownLoc().getLatitude());
        ///alert.longitude = String.valueOf(preferences.getLastKnownLoc().getLongitude());
        alert.createDate = UTILITY.longToString(new Date().getTime());
        alert.updateDate = UTILITY.longToString(new Date().getTime());
        alert.status = 1;
        Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                Location location = CurrentLocation.get(getBaseContext());
                alert.latitude = String.valueOf(location.getLatitude());
                alert.longitude = String.valueOf(location.getLongitude());
                if (new SendAlert().send(alert)) {
                    e.onSuccess(true);
                } else {
                    e.onError(new Exception("Error"));
                }
                savePosition(alert, preferences, location);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onError(Throwable e) {
                        cancelButton.setEnabled(true);
                        cancelButton.setText("SALIR");
                        countSend.setText("GUARDADO");
                        getPreferences().setAlert(alert);
                        AlertSyncJob.jobScheduler(getApplicationContext());
                    }

                    @Override
                    public void onSuccess(Boolean isSuccess) {
                        cancelButton.setEnabled(true);
                        cancelButton.setText("SALIR");
                        countSend.setText("ENVIADA");
                    }
                });
    }

    private void savePosition(Alert alert, AppPreferences preferences, Location location) {
        final TabletPosition position = new TabletPosition(location, getImei());
        position.generatedTime = alert.createDate;
        position.watchId = preferences.getWatch().id;
        position.message = TabletPosition.MESSAGE.SOS1.name();
        position.isException = true;
        position.alertMessage = alert.message;
        AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().insert(position);
    }

    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    public void clearSession() {
        getPreferences().clearWatch();
        startActivity(new Intent(this, LoginActivity.class));
        stopService(new Intent(this, TrackingService.class));
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUserUnauthorized(OnUserUnauthorized event) {
        EventBus.getDefault().removeStickyEvent(OnUserUnauthorized.class);
        if (dialog != null)
            dialog.dismiss();

        Toast.makeText(this, "Sesión Expirada", Toast.LENGTH_LONG).show();
        clearSession();
    }

    protected void showSnackbar(@NonNull View view, @NonNull String content) {
        Snackbar snack = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
        View sbView = snack.getView();
        sbView.setBackground(getResources().getDrawable(R.drawable.snack_background));
        snack.show();
    }

    protected void showSnackbarLong(@NonNull View view, @NonNull String content) {
        Snackbar snack = Snackbar.make(view, content, Snackbar.LENGTH_LONG);
        View sbView = snack.getView();
        sbView.setBackground(getResources().getDrawable(R.drawable.snack_background));
        snack.show();
    }
}
