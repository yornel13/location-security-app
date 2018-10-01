package com.icsseseguridad.locationsecurity.view.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.background.PositionIntentService;
import com.icsseseguridad.locationsecurity.service.background.RepoIntentService;
import com.icsseseguridad.locationsecurity.service.background.TrackingService;
import com.icsseseguridad.locationsecurity.service.entity.Banner;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.event.OnFinishWatchFailure;
import com.icsseseguridad.locationsecurity.service.event.OnFinishWatchSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.service.repository.BinnacleController;
import com.icsseseguridad.locationsecurity.service.repository.MessengerController;
import com.icsseseguridad.locationsecurity.service.repository.TabletPositionController;
import com.icsseseguridad.locationsecurity.service.repository.WatchController;
import com.icsseseguridad.locationsecurity.service.synchronizer.AlertSyncJob;
import com.icsseseguridad.locationsecurity.service.synchronizer.MainSyncJob;
import com.icsseseguridad.locationsecurity.util.MyDescriptionAnimation;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.view.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.view.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.viewmodel.BannerListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VisitListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.slider) SliderLayout sliderView;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    private Location location;
    private QBadgeView badgeChat;
    private QBadgeView badgeBinnacle;

    private BannerListViewModel bannerListViewModel;

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

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        badgeChat = new QBadgeView(this);
        badgeChat.bindTarget(bottomNavigationMenuView.getChildAt(3));

        badgeBinnacle = new QBadgeView(this);
        badgeBinnacle.bindTarget(bottomNavigationMenuView.getChildAt(1));

        new MessengerController().getUnreadMessages();
        new BinnacleController().getUnreadReports();

        bannerListViewModel = ViewModelProviders
                .of(this)
                .get(BannerListViewModel.class);

        bannerListViewModel.getBanners().observe(this, new Observer<List<Banner>>() {
            @Override
            public void onChanged(@Nullable final List<Banner> banners) {
                setupBanners(banners);
            }
        });

//        {
//            RepoIntentService.run(this);
//            PositionIntentService.run(this);
//            AlertSyncJob.jobScheduler(this);
//        }
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

        TrackingService.start(this);

        if (app.unreadMessages != null) {
            if (app.unreadMessages.unread > 0) {
                badgeChat.setBadgeNumber(app.unreadMessages.unread);
            } else {
                if (badgeChat.getBadgeNumber() > 0)
                    badgeChat.hide(true);
                badgeChat.setBadgeNumber(0);
            }
        }

        if (app.unreadReplies != null) {
            if (app.unreadReplies.unread > 0) {
                badgeBinnacle.setBadgeNumber(app.unreadReplies.unread);
            } else {
                if (badgeBinnacle.getBadgeNumber() > 0)
                    badgeBinnacle.hide(true);
                badgeBinnacle.setBadgeNumber(0);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncUnreadMessages(OnSyncUnreadMessages event) {
        onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncUnreadReplies(OnSyncUnreadReplies event) {
        onResume();
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
        RepoIntentService.run(this);
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
        //updatePosition(getPreferences().getWatch().id, getPreferences().getImei());
        getPreferences().clearWatch();
        startActivity(new Intent(this, LoginActivity.class));
        stopService(new Intent(this, TrackingService.class));
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
                        RepoIntentService.run(MainActivity.this);
                        clearSession();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do
                    }
                }).show();
    }

    public void setupBanners(List<Banner> banners) {
        if (banners.size() > 0) {
            sliderView.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions().centerCrop();
            for (int i = 0; i < banners.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .image(banners.get(i).photo)
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
