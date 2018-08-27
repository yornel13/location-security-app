package com.icsseseguridad.locationsecurity.ui.binnacle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.ReportAdapter;
import com.icsseseguridad.locationsecurity.adapter.VisitAdapter;
import com.icsseseguridad.locationsecurity.controller.BinnacleController;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnClickReport;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportFailure;
import com.icsseseguridad.locationsecurity.events.OnListGuardReportSuccess;
import com.icsseseguridad.locationsecurity.model.ControlVisit;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.ui.MainActivity;
import com.icsseseguridad.locationsecurity.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BinnacleActivity extends BaseActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    public static final Integer INTENT_REGISTER_REPORT = 11;
    public static final Integer INTENT_SHOW_REPORT = 12;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;
    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.empty) View emptyView;

    private ReportAdapter adapter;

    List<SpecialReport> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binnacle);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        setupAdapter(new ArrayList<SpecialReport>());
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        new BinnacleController().getGuardReports(getPreferences().getGuard().id);

        bottomNavigationView.setSelectedItemId(R.id.nav_binnacle);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void setupAdapter(List<SpecialReport> reports) {
        this.reports = reports;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ReportAdapter(this, reports);
        recyclerView.setAdapter(adapter);
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
                        startActivity(new Intent(BinnacleActivity.this, VisitsActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_chat:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(BinnacleActivity.this, MessageActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_alert:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(BinnacleActivity.this, AlertsActivity.class));
                    }
                }, 100);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REGISTER_REPORT && resultCode == RESULT_OK) {
            Snackbar.make(toolbar, "Reporte Enviado con Exito.", Snackbar.LENGTH_LONG).show();
            if (reports != null) {
                reports.add(0, app.report);
                checkView();
                app.report = null;
            }
        }
        if (requestCode == INTENT_SHOW_REPORT && resultCode == RESULT_OK) {
            Snackbar.make(toolbar, "Visita Finaliza con Exito.", Snackbar.LENGTH_LONG).show();
            if (reports != null) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameText.setText(getPreferences().getGuard().getFullname());
        dateText.setText(UTILITY.getCurrentDate());
    }

    @OnClick(R.id.add_button)
    public void addVisit() {
        startActivityForResult(new Intent(this, AddReportActivity.class), INTENT_REGISTER_REPORT);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListGuardReportFailure(OnListGuardReportFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListGuardReportFailure.class);
        loadingView.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListGuardReportSuccess(OnListGuardReportSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListGuardReportSuccess.class);
        reports = event.list.reports;
        checkView();
    }

    public void checkView() {
        loadingView.setVisibility(View.GONE);
        if (reports.size() > 0) {
            emptyView.setVisibility(View.GONE);
            adapter.replaceAll(reports);
        } else {
            adapter.replaceAll(new ArrayList<SpecialReport>());
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickVisit(OnClickReport event) {
        app.report = event.report;
        startActivityForResult(new Intent(this, ReportActivity.class), INTENT_SHOW_REPORT);
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}
