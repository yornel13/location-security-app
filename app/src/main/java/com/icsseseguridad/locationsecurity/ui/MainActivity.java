package com.icsseseguridad.locationsecurity.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.BannerController;
import com.icsseseguridad.locationsecurity.controller.MessengerController;
import com.icsseseguridad.locationsecurity.controller.TabletPositionController;
import com.icsseseguridad.locationsecurity.controller.WatchController;
import com.icsseseguridad.locationsecurity.events.OnFinishWatchFailure;
import com.icsseseguridad.locationsecurity.events.OnFinishWatchSuccess;
import com.icsseseguridad.locationsecurity.events.OnGetBannersSuccess;
import com.icsseseguridad.locationsecurity.model.Alert;
import com.icsseseguridad.locationsecurity.model.TabletPosition;
import com.icsseseguridad.locationsecurity.model.Watch;
import com.icsseseguridad.locationsecurity.service.LocationService;
import com.icsseseguridad.locationsecurity.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.MyDescriptionAnimation;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.slider) SliderLayout sliderView;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                navigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(Gravity.START, false);
                        MainActivity.this.onNavigationItemSelected(menuItem);
                    }
                }, 250);
                return true;
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                Log.d("MainActivity", "Token Registration: "+deviceToken);
                new MessengerController().register(getImei(), deviceToken, getPreferences().getGuard().id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MainActivity", "Failed to get registration Token");
            }
        });

        new BannerController().getBanners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameText.setText(getPreferences().getGuard().getFullname());
        dateText.setText(UTILITY.getCurrentDate());
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        if (!isServiceRunning(LocationService.class))
            startService(new Intent(this, LocationService.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_exit:
                dialogCloseSession();
                break;
            case R.id.nav_finish:
                dialogFinishWatch();
                break;
            case R.id.nav_visit:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, VisitsActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_binnacle:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, BinnacleActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_chat:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, MessageActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_alert:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, AlertsActivity.class));
                    }
                }, 100);
                break;
        }
        return true;
    }

    @OnClick(R.id.visits)
    public void goVisit() {
        bottomNavigationView.setSelectedItemId(R.id.nav_visit);
    }

    @OnClick(R.id.messages)
    public void goMessages() {
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
    }

    @OnClick(R.id.binnacle)
    public void goBinnacle() {
        bottomNavigationView.setSelectedItemId(R.id.nav_binnacle);
    }

    @OnClick(R.id.alerts)
    public void goAlerts() {
        bottomNavigationView.setSelectedItemId(R.id.nav_alert);
    }

    @OnClick({R.id.sos, R.id.sos_alarm})
    public void SOS() {
        dialogSOS();
    }

    @OnClick(R.id.finish_watch)
    public void finishWatch() {
        dialogFinishWatch();
    }

    public void dialogFinishWatch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setMessage(R.string.finish_watch_text_dialog)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestFinishWatch();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do
                    }
                }).show();
    }

    public void requestFinishWatch() {
        builderDialog.text("Finalizando");
        dialog.show();
        location = getLastKnowLocation();
        WatchController watchController = new WatchController();
        watchController.finish(getPreferences().getWatch().id,
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()), null);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFinishWatchSuccess(OnFinishWatchSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnFinishWatchSuccess.class);
        dialog.dismiss();
        Toast.makeText(this, "Guardia Finalizada", Toast.LENGTH_LONG).show();
        updatePosition(getPreferences().getWatch().id, getPreferences().getImei());
        getPreferences().clearWatch();
        getPreferences().clearWatch();
        startActivity(new Intent(this, LoginActivity.class));
        stopService(new Intent(this, LocationService.class));
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFinishWatchFailure(OnFinishWatchFailure event) {
        EventBus.getDefault().removeStickyEvent(OnFinishWatchFailure.class);
        dialog.dismiss();
        final Snackbar snackbar = Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INTENTAR DENUEVO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                requestFinishWatch();
            }
        });
        snackbar.show();
    }

    private void updatePosition(Long watchId, String imei) {
        TabletPositionController positionController = new TabletPositionController();
        TabletPosition tabletPosition = new TabletPosition(location, imei);
        tabletPosition.message = TabletPosition.MESSAGE.FINISHED_WATCH.name();
        tabletPosition.watchId = watchId;
        positionController.register(tabletPosition, false);
    }

    public void dialogCloseSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setMessage(R.string.close_user)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearSession();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do
                    }
                }).show();
    }

    public void clearSession() {
        getPreferences().clearWatch();
        startActivity(new Intent(this, LoginActivity.class));
        stopService(new Intent(this, LocationService.class));
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnGetBannersSuccess(OnGetBannersSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnGetBannersSuccess.class);
        if (event.list.total > 0) {
            sliderView.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            for (int i = 0; i < event.list.total; i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .image(event.list.banners.get(i).photo)
                        .setRequestOption(requestOptions)
                        .setBackgroundColor(Color.WHITE)
                        .setProgressBarVisible(true);
                textSliderView.bundle(new Bundle());
                sliderView.addSlider(textSliderView);
            }
            sliderView.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderView.setCustomAnimation(new MyDescriptionAnimation());
            sliderView.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderView.setDuration(5000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderView.stopAutoCycle();
    }
}