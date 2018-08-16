package com.icsseseguridad.locationsecurity.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.ui.binnacle.AddReportActivity;
import com.icsseseguridad.locationsecurity.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final Integer INTENT_NEW_CHAT = 11;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);



        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
                        startActivity(new Intent(MessageActivity.this, VisitsActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_binnacle:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(MessageActivity.this, BinnacleActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_alert:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(MessageActivity.this, AlertsActivity.class));
                    }
                }, 100);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameText.setText(preferences.getGuard().getFullname());
        dateText.setText(UTILITY.getCurrentDate());
    }

    @OnClick(R.id.add_button)
    public void addVisit() {
        startActivityForResult(new Intent(this, ChatActivity.class), INTENT_NEW_CHAT);
    }
}
