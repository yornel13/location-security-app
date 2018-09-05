package com.icsseseguridad.locationsecurity.ui;

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
import com.icsseseguridad.locationsecurity.controller.AlertController;
import com.icsseseguridad.locationsecurity.events.OnSendAlertFailure;
import com.icsseseguridad.locationsecurity.events.OnSendAlertSuccess;
import com.icsseseguridad.locationsecurity.model.Alert;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        Alert alert = new Alert();
        alert.cause = Alert.CAUSE.DROP;
        alert.type = Alert.CAUSE.DROP;
        AppPreferences preferences = new AppPreferences(getApplicationContext());
        alert.message = "Alerta de posible caida del guardia: "+preferences.getGuard().getFullname();
        alert.guardId = preferences.getGuard().id;
        alert.latitude = String.valueOf(preferences.getLastKnownLoc().getLatitude());
        alert.longitude = String.valueOf(preferences.getLastKnownLoc().getLongitude());
        new AlertController().send(alert);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void sendAlertFailure(OnSendAlertFailure event) {
        EventBus.getDefault().removeStickyEvent(OnSendAlertFailure.class);
        countSend.setText("FALLIDO");
        cancelButton.setEnabled(true);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void sendAlertSuccess(OnSendAlertSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnSendAlertSuccess.class);
        cancelButton.setEnabled(true);
        cancelButton.setText("SALIR");
        countSend.setText("ENVIADA");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
