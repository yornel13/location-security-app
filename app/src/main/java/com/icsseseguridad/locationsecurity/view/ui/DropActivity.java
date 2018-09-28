package com.icsseseguridad.locationsecurity.view.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.repository.AlertController;
import com.icsseseguridad.locationsecurity.service.event.OnSendAlertFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSendAlertSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.synchronizer.AlertSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.SendAlert;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new AppPreferences(getApplicationContext()).isDrop())
            dialogDrop();
        else
            finish();
    }

    private AlertDialog dialogDrop;
    private Button cancelButton;
    private TextView countSend;
    private TextView countTitle;
    private int i;
    public void dialogDrop() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);
        dialogBuilder.setView(dialogView);
        i = 0;
        countSend = dialogView.findViewById(R.id.count);
        countTitle = dialogView.findViewById(R.id.title);
        countTitle.setText("CAIDA DETECTADA, CUENTA REGRESIVA PARA ENVIAR ALERTA");
        final ProgressBar progressBar = dialogView.findViewById(R.id.progress);
        cancelButton = dialogView.findViewById(R.id.button);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        countSend.setText("5");
        progressBar.setVisibility(View.VISIBLE);
        final CountDownTimer mCountDownTimer = new CountDownTimer(6000,10) {

            @Override
            public void onTick(long millisUntilFinished) {
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
        dialogDrop = dialogBuilder.create();
        dialogDrop.setCanceledOnTouchOutside(false);
        dialogDrop.setCancelable(false);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountDownTimer.cancel();
                dialogDrop.dismiss();
                finish();
            }
        });
        mCountDownTimer.start();
        dialogDrop.show();
    }

    public void sendAlert() {
        final Alert alert = new Alert();
        alert.cause = Alert.CAUSE.DROP;
        alert.type = Alert.CAUSE.DROP;
        final AppPreferences preferences = new AppPreferences(getApplicationContext());
        alert.message = "Alerta de posible caida del guardia: "+preferences.getGuard().getFullname();
        alert.guardId = preferences.getGuard().id;
        alert.latitude = String.valueOf(preferences.getLastKnownLoc().getLatitude());
        alert.longitude = String.valueOf(preferences.getLastKnownLoc().getLongitude());
        alert.createDate = new Timestamp(new Date().getTime());
        alert.updateDate = new Timestamp(new Date().getTime());
        alert.status = 1;
        Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                if (new SendAlert().send(alert)) {
                    e.onSuccess(true);
                } else {
                    e.onError(new Exception("Error"));
                }
                savePosition(alert, preferences);
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
                        preferences.setAlert(alert);
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

    private void savePosition(Alert alert, AppPreferences preferences) {
        final TabletPosition position = new TabletPosition(preferences.getLastKnownLoc(), preferences.getImei());
        position.generatedTime = new Timestamp(new Date().getTime());
        position.watchId = preferences.getWatch().id;
        position.message = TabletPosition.MESSAGE.DROP.name();
        position.isException = true;
        position.alertMessage = alert.message;
        AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().insert(position);
    }


//    public void sendAlert() {
//        Alert alert = new Alert();
//        alert.cause = Alert.CAUSE.DROP;
//        alert.type = Alert.CAUSE.DROP;
//        AppPreferences preferences = new AppPreferences(getApplicationContext());
//        alert.message = "Alerta de posible caida del guardia: "+preferences.getGuard().getFullname();
//        alert.guardId = preferences.getGuard().id;
//        alert.latitude = String.valueOf(preferences.getLastKnownLoc().getLatitude());
//        alert.longitude = String.valueOf(preferences.getLastKnownLoc().getLongitude());
//        new AlertController().send(alert);
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void sendAlertFailure(OnSendAlertFailure event) {
//        EventBus.getDefault().removeStickyEvent(OnSendAlertFailure.class);
//        countSend.setText("FALLIDO");
//        cancelButton.setEnabled(true);
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void sendAlertSuccess(OnSendAlertSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnSendAlertSuccess.class);
//        cancelButton.setEnabled(true);
//        cancelButton.setText("SALIR");
//        countSend.setText("ENVIADA");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
}
