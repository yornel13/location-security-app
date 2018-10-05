package com.icsseseguridad.locationsecurity.view.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.background.TrackingService;
import com.icsseseguridad.locationsecurity.service.event.OnVerifyTabletFailure;
import com.icsseseguridad.locationsecurity.service.event.OnVerifyTabletSuccess;
import com.icsseseguridad.locationsecurity.service.repository.AuthController;
import com.icsseseguridad.locationsecurity.service.repository.TabletPositionController;
import com.icsseseguridad.locationsecurity.service.repository.WatchController;
import com.icsseseguridad.locationsecurity.service.event.OnInitWatchFailure;
import com.icsseseguridad.locationsecurity.service.event.OnInitWatchSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnRegisteredTabletFailure;
import com.icsseseguridad.locationsecurity.service.event.OnRegisteredTabletSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSignAdminSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSignInFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSignInSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnVerifySessionFailure;
import com.icsseseguridad.locationsecurity.service.event.OnVerifySessionSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.Watch;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "LoginActivity";

    private static final int INTENT_TURN_ON_GPS = 65;
    @BindView(R.id.background_login) ImageView background;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.background_login_2) ImageView background2;
    @BindView(R.id.container_2) RelativeLayout container2;
    @BindView(R.id.dni) EditText dniText;
    @BindView(R.id.password) EditText passwordText;
    @BindView(R.id.login) Button loginButton;
    @BindView(R.id.imei) TextView imeiText;
    @BindView(R.id.version) TextView versionText;

    private Guard guard;

    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient client;

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
        connectGoogleApi();

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            versionText.setText("Versión " + versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        requestLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
            Log.e("Token", getPreferences().getToken());
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
                showView();
            }
        });
        container2.startAnimation(animation);
    }

    public void showView() {
        container2.setVisibility(View.GONE);
        if (getPreferences().isRegistered()) {
            imeiText.setText("IMEI " + getPreferences().getImei());
        } else {
            loginButton.setText("Registrar Dispositivo");
            builderDialog.text("Verificando");
            dialog.show();
            new AuthController().verifyTablet(getImei());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void verifyTabletSuccess(OnVerifyTabletSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnVerifyTabletSuccess.class);
        dialog.dismiss();
        getPreferences().setImei(event.tablet.imei);
        getPreferences().setRegistered();
        loginButton.setText("Iniciar Guardia");
        imeiText.setText("IMEI " + getPreferences().getImei());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void verifyTabletFailure(OnVerifyTabletFailure event) {
        EventBus.getDefault().removeStickyEvent(OnVerifyTabletFailure.class);
        dialog.dismiss();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void signInSuccess(OnSignInSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnSignInSuccess.class);
        builderDialog.text("Iniciando");
        guard = event.guard;
        WatchController watchController = new WatchController();
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
        getPreferences().setLastKnownLoc(location);
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
        location = null;
        builderDialog.text("Iniciando");
        dialog.show();
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                location = CurrentLocation.get(LoginActivity.this, false);
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        continueLogin();
                    }
                }).isDisposed();
    }

    public void signInAdmin() {
        AuthController authController = new AuthController();
        authController.singInAdmin(dniText.getText().toString(), passwordText.getText().toString());
        builderDialog.text("Registrando");
        dialog.show();
    }

    public void continueLogin() {
        if (location == null) {
            Toast.makeText(this, "Problemas para obtener ubicación.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        dniText.setEnabled(false);
        passwordText.setEnabled(false);
        AuthController authController = new AuthController();
        authController.singIn(dniText.getText().toString(), passwordText.getText().toString());
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
            stopService(new Intent(this, TrackingService.class));
        }
        if (client != null)
            client.removeLocationUpdates(callback);
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

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, callback, null);
        }
    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(TAG, String.valueOf(location.getLatitude())
                        + ", " + String.valueOf(location.getLongitude()));
            }
        }
    };
}
