package com.icsseseguridad.locationsecurity.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.AuthController;
import com.icsseseguridad.locationsecurity.controller.TabletPositionController;
import com.icsseseguridad.locationsecurity.controller.WatchController;
import com.icsseseguridad.locationsecurity.events.OnInitWatchFailure;
import com.icsseseguridad.locationsecurity.events.OnInitWatchSuccess;
import com.icsseseguridad.locationsecurity.events.OnRegisteredTabletFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisteredTabletSuccess;
import com.icsseseguridad.locationsecurity.events.OnSignAdminSuccess;
import com.icsseseguridad.locationsecurity.events.OnSignInFailure;
import com.icsseseguridad.locationsecurity.events.OnSignInSuccess;
import com.icsseseguridad.locationsecurity.events.OnVerifySessionFailure;
import com.icsseseguridad.locationsecurity.events.OnVerifySessionSuccess;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.TabletPosition;
import com.icsseseguridad.locationsecurity.model.Watch;
import com.icsseseguridad.locationsecurity.service.LocationService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    private static final int INTENT_TURN_ON_GPS = 65;
    @BindView(R.id.background_login) ImageView background;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.background_login_2) ImageView background2;
    @BindView(R.id.container_2) RelativeLayout container2;
    @BindView(R.id.dni) EditText dniText;
    @BindView(R.id.password) EditText passwordText;
    @BindView(R.id.login) Button loginButton;
    @BindView(R.id.imei) TextView imeiText;

    private Guard guard;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        container2.setVisibility(View.VISIBLE);

        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        initApp();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(LoginActivity.this, "No tienes permisos", Toast.LENGTH_LONG).show();
                    }
                })
                .setDeniedMessage("Si rechaza los permisos, no podras usar la aplicacion" +
                        "\n\nPor favor, activa los permisos en [Configuración] > [Permisos]")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA)
                .check();

        passwordText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    login(null);
                    return true;
                }
                return false;
            }
        });

        if (!getPreferences().isRegistered()) {
            loginButton.setText("Registrar Dispositivo");
        }
    }

    private void turnGPSOn() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText(this, "Debes activar el GPS", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), INTENT_TURN_ON_GPS);
        } else {
            loadView();
        }
    }

    void initApp() {
        container2.postDelayed(new Runnable() {
            @Override
            public void run() {
                turnGPSOn();
            }
        }, 2000);
    }

    public void loadView() {
        if (getPreferences().getGuard() != null) {
            new AuthController().verify();
            builderDialog.text("Autenticando");
            dialog.show();
        } else {
            vanishImage();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void verifySessionSuccess(OnVerifySessionSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnVerifySessionSuccess.class);
        dialog.dismiss();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void verifySessionFailure(OnVerifySessionFailure event) {
        EventBus.getDefault().removeStickyEvent(OnVerifySessionFailure.class);
        dialog.dismiss();
        Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
        finish();
    }

    private void vanishImage() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                container2.setVisibility(View.GONE);
                if (getPreferences().isRegistered()) {
                    imeiText.setText("IMEI " + getImei());
                }
            }
        });
        container2.startAnimation(animation);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void signInSuccess(OnSignInSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnSignInSuccess.class);
        builderDialog.text("Iniciando");
        guard = event.guard;
        WatchController watchController = new WatchController();
        location = getLastKnowLocation();
        watchController.register(
                guard.token,
                guard.id,
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                getImei());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void signInFailure(OnSignInFailure event) {
        EventBus.getDefault().removeStickyEvent(OnSignInFailure.class);
        Toast.makeText(this, event.message, Toast.LENGTH_LONG).show();
        dniText.setEnabled(true);
        passwordText.setEnabled(true);
        dialog.dismiss();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void initWatchSuccess(OnInitWatchSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnInitWatchSuccess.class);
        dialog.dismiss();
        getPreferences().setGuard(guard);
        getPreferences().setWatch(event.watch);
        //updatePosition(event.watch);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void initWatchFailure(OnInitWatchFailure event) {
        EventBus.getDefault().removeStickyEvent(OnInitWatchFailure.class);
        Toast.makeText(this, event.message, Toast.LENGTH_LONG).show();
        dniText.setEnabled(true);
        passwordText.setEnabled(true);
        dialog.dismiss();
    }

    private void updatePosition(Watch watch) {
        TabletPositionController positionController = new TabletPositionController();
        TabletPosition tabletPosition = new TabletPosition(location, getImeiAndSave());
        if (watch.resumed)
            tabletPosition.message = TabletPosition.MESSAGE.RESUMED_WATCH.name();
        else
            tabletPosition.message = TabletPosition.MESSAGE.INIT_WATCH.name();
        tabletPosition.watchId = watch.id;
        positionController.register(tabletPosition, false);
    }

    public void login(View view) {
        if (dniText.getText().toString().isEmpty()) {
            dniText.setError(getString(R.string.error_empty_label));
            dniText.requestFocus();
            return;
        }
        if (passwordText.getText().toString().isEmpty()) {
            passwordText.setError(getString(R.string.error_empty_label));
            dniText.requestFocus();
            return;
        }

        hideKeyboard();

        if (!getPreferences().isRegistered()) {
            signInAdmin();
            return;
        }

        if (!isServiceRunning(LocationService.class)) {
            startService(new Intent(this, LocationService.class));
            Toast.makeText(this, "Iniciando servicio de Localización.", Toast.LENGTH_SHORT).show();
            builderDialog.text("Iniciando");
            dialog.show();
            passwordText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    continueLogin(false);
                }
            }, 5000);
            return;
        }
        continueLogin(true);
    }

    public void signInAdmin() {
        AuthController authController = new AuthController();
        authController.singInAdmin(dniText.getText().toString(), passwordText.getText().toString());
        builderDialog.text("Registrando");
        dialog.show();
    }

    public void continueLogin(boolean showDialog) {
        if (getLastKnowLocation() == null) {
            Toast.makeText(this, "Espere unos segundos que el GPS lo ubique y vuelva a intentar.", Toast.LENGTH_SHORT).show();
            if (!showDialog) dialog.dismiss();
            return;
        }
        dniText.setEnabled(false);
        passwordText.setEnabled(false);
        AuthController authController = new AuthController();
        authController.singIn(dniText.getText().toString(), passwordText.getText().toString());
        if (showDialog) {
            builderDialog.text("Iniciando");
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_TURN_ON_GPS) {
            turnGPSOn();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getPreferences().getWatch() == null) {
            stopService(new Intent(this, LocationService.class));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void signAdminSuccess(OnSignAdminSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnSignAdminSuccess.class);
        new AuthController().registeredTablet(getImei());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void registeredTabletSuccess(OnRegisteredTabletSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnRegisteredTabletSuccess.class);
        dialog.dismiss();
        Toast.makeText(this, "Tablet Registrada con Exito!", Toast.LENGTH_SHORT).show();
        getPreferences().setRegistered();
        finish();
        startActivity(getIntent());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void registeredTabletFailure(OnRegisteredTabletFailure event) {
        EventBus.getDefault().removeStickyEvent(OnSignInFailure.class);
        Toast.makeText(this, event.response, Toast.LENGTH_LONG).show();
        dniText.setEnabled(true);
        passwordText.setEnabled(true);
        dialog.dismiss();
    }
}
