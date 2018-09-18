package com.icsseseguridad.locationsecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.events.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.events.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;

public class AlertsActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    private QBadgeView badgeChat;
    private QBadgeView badgeBinnacle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);



        bottomNavigationView.setSelectedItemId(R.id.nav_alert);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        badgeChat = new QBadgeView(this);
        badgeChat.bindTarget(bottomNavigationMenuView.getChildAt(3));
        badgeBinnacle = new QBadgeView(this);
        badgeBinnacle.bindTarget(bottomNavigationMenuView.getChildAt(1));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);
                break;
            case R.id.nav_visit:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(AlertsActivity.this, VisitsActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_binnacle:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(AlertsActivity.this, BinnacleActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_chat:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(AlertsActivity.this, MessageActivity.class));
                    }
                }, 100);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameText.setText(getPreferences().getGuard().getFullname());
        dateText.setText(UTILITY.getCurrentDate());

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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}
