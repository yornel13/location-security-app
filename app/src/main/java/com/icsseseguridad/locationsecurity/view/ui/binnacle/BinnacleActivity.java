package com.icsseseguridad.locationsecurity.view.ui.binnacle;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.entity.ReportWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.service.event.OnClickReport;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.service.synchronizer.MainSyncJob;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.adapter.ReportAdapter;
import com.icsseseguridad.locationsecurity.view.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.view.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.view.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.viewmodel.BinnacleListViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import q.rorbin.badgeview.QBadgeView;

public class BinnacleActivity extends BaseActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    public static final int INTENT_REGISTER_REPORT = 11;
    public static final int INTENT_SHOW_REPORT = 12;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;
    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.empty) View emptyView;
    @BindView(R.id.search_field) EditText searchField;
    @BindView(R.id.placeSnackBar) View placeSnackbar;

    private ReportAdapter adapter;
    private QBadgeView badgeChat;
    private QBadgeView badgeBinnacle;

    List<SpecialReport> reports;

    private BinnacleListViewModel binnacleListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binnacle);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        setupAdapter(new ArrayList<SpecialReport>());
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        searchField.setVisibility(View.GONE);
        // new BinnacleController().getGuardReports(getPreferences().getGuard().id);

        bottomNavigationView.setSelectedItemId(R.id.nav_binnacle);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        badgeChat = new QBadgeView(this);
        badgeChat.bindTarget(bottomNavigationMenuView.getChildAt(3));

        badgeBinnacle = new QBadgeView(this);
        badgeBinnacle.bindTarget(bottomNavigationMenuView.getChildAt(1));

        binnacleListViewModel = ViewModelProviders
                .of(this)
                .get(BinnacleListViewModel.class);

        binnacleListViewModel.getVisits().observe(this, new Observer<List<SpecialReport>>() {
            @Override
            public void onChanged(@Nullable final List<SpecialReport> reports) {
                BinnacleActivity.this.reports = reports;
                checkView();
            }
        });
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_REGISTER_REPORT:
                    showSnackbarLong(placeSnackbar, "Reporte creado con exito.");
                    if (reports != null) {
                        reports.add(0, app.report);
                        checkView();
                        app.report = null;
                    }
                    break;
                case INTENT_SHOW_REPORT:
                    break;
            }
            MainSyncJob.jobScheduler(this);
        }
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
        checkView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncUnreadMessages(OnSyncUnreadMessages event) {
        onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncUnreadReplies(OnSyncUnreadReplies event) {
        onResume();
    }

    @OnClick(R.id.add_button)
    public void addVisit() {
        startActivityForResult(new Intent(this, AddReportActivity.class), INTENT_REGISTER_REPORT);
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onListGuardReportFailure(OnListGuardReportFailure event) {
//        EventBus.getDefault().removeStickyEvent(OnListGuardReportFailure.class);
//        loadingView.setVisibility(View.GONE);
//        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onListGuardReportSuccess(OnListGuardReportSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnListGuardReportSuccess.class);
//        reports = event.list.reports;
//        checkView();
//    }

    public void checkView() {
        loadingView.setVisibility(View.GONE);
        if (reports.size() > 0) {
            emptyView.setVisibility(View.GONE);
            checkUnread();
            orderList();
            adapter.replaceAll(reports);
            searchField.setText("");
            searchField.setVisibility(View.VISIBLE);
        } else {
            adapter.replaceAll(new ArrayList<SpecialReport>());
            emptyView.setVisibility(View.VISIBLE);
            searchField.setVisibility(View.GONE);
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

    void checkUnread() {
        if (app.unreadReplies != null && app.unreadReplies.reportsUnread != null)
            for (SpecialReport report: reports) {
                report.unread = 0;
                for (ReportWithUnread reportWithUnread : app.unreadReplies.reportsUnread) {
                    if (report.id.longValue() == reportWithUnread.report.id.longValue()) {
                        report.unread = reportWithUnread.unread;
                    }
                }
            }
    }

    public void orderList() {
        Collections.sort(reports, new Comparator<SpecialReport>(){
            public int compare(SpecialReport obj1, SpecialReport obj2) {
                return obj2.unread.compareTo(obj1.unread);
            }
        });
    }

    @OnTextChanged(R.id.search_field)
    protected void onTextChanged(CharSequence text) {
        String filter = text.toString();
        if (filter.isEmpty()) {
            adapter.setItems(reports);
        } else {
            adapter.setItems(filter(filter));
        }
    }

    public ArrayList<SpecialReport> filter(String text) {
        ArrayList<SpecialReport> filteredList = new ArrayList<>();
        for (SpecialReport item : reports) {
            if (normalize(item.title).contains(normalize(text))
                    || normalize(item.observation).contains(normalize(text))){
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private String normalize(String input) {
        if (input == null) { return ""; }
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^a-zA-Z0-9]+","").toLowerCase();
    }
}
