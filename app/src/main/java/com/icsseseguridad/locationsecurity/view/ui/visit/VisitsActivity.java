package com.icsseseguridad.locationsecurity.view.ui.visit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.synchronizer.MainSyncJob;
import com.icsseseguridad.locationsecurity.view.adapter.VisitAdapter;
import com.icsseseguridad.locationsecurity.service.event.OnClickVisit;
import com.icsseseguridad.locationsecurity.service.event.OnListActiveVisitFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListActiveVisitSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.view.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.view.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.view.ui.chat.MessageActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.viewmodel.VisitListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import q.rorbin.badgeview.QBadgeView;

public class VisitsActivity extends BaseActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int INTENT_REGISTER_VISIT = 11;
    public static final int INTENT_SHOW_VISIT = 12;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;
    @BindView(R.id.search_field) EditText searchField;

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.empty) View emptyView;

    private VisitAdapter adapter;
    private QBadgeView badgeChat;
    private QBadgeView badgeBinnacle;

    List<ControlVisit> visits;

    private VisitListViewModel visitListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        setupAdapter(new ArrayList<ControlVisit>());
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        searchField.setVisibility(View.GONE);
        // new VisitController().getActiveVisits();

        bottomNavigationView.setSelectedItemId(R.id.nav_visit);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        badgeChat = new QBadgeView(this);
        badgeChat.bindTarget(bottomNavigationMenuView.getChildAt(3));

        badgeBinnacle = new QBadgeView(this);
        badgeBinnacle.bindTarget(bottomNavigationMenuView.getChildAt(1));

        visitListViewModel = ViewModelProviders
                .of(this)
                .get(VisitListViewModel.class);

        visitListViewModel.getVisits().observe(this, new Observer<List<ControlVisit>>() {
            @Override
            public void onChanged(@Nullable final List<ControlVisit> visits) {
                VisitsActivity.this.visits = visits;
                checkView();
            }
        });
    }

    private void setupAdapter(List<ControlVisit> visits) {
        this.visits = visits;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new VisitAdapter(this, visits);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
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
            case R.id.nav_binnacle:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(VisitsActivity.this, BinnacleActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_chat:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(VisitsActivity.this, MessageActivity.class));
                    }
                }, 100);
                break;
            case R.id.nav_alert:
                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(VisitsActivity.this, AlertsActivity.class));
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

    @OnClick(R.id.add_button)
    public void addVisit() {
        startActivityForResult(new Intent(this, AddVisitActivity.class), INTENT_REGISTER_VISIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_REGISTER_VISIT:
                    Snackbar.make(toolbar, "Visita Registrada con Exito.", Snackbar.LENGTH_LONG).show();
                    if (visits != null) {
                        if (app != null && app.visit != null) {
                            visits.add(app.visit);
                            app.visit = null;
                        }
                    }
                    break;
                case INTENT_SHOW_VISIT:
                    Snackbar.make(toolbar, "Visita Finaliza con Exito.", Snackbar.LENGTH_LONG).show();
                    break;
            }
            MainSyncJob.jobScheduler(this);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListActiveVisitFailure(OnListActiveVisitFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListActiveVisitFailure.class);
        loadingView.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListActiveVisitSuccess(OnListActiveVisitSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListActiveVisitSuccess.class);
        visits = event.list.visits;
        checkView();
    }

    public void checkView() {
        loadingView.setVisibility(View.GONE);
        if (visits.size() > 0) {
            emptyView.setVisibility(View.GONE);
            adapter.replaceAll(visits);
            searchField.setText("");
            searchField.setVisibility(View.VISIBLE);
        } else {
            adapter.replaceAll(new ArrayList<ControlVisit>());
            emptyView.setVisibility(View.VISIBLE);
            searchField.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickVisit(OnClickVisit event) {
        // app.visit = event.controlVisit;
        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra(ControlVisit.class.getName(), gson().toJson(event.controlVisit));
        startActivityForResult(intent, INTENT_SHOW_VISIT);
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }

    @OnTextChanged(R.id.search_field)
    protected void onTextChanged(CharSequence text) {
        String filter = text.toString();
        if (filter.isEmpty()) {
            adapter.setItems(visits);
        } else {
            adapter.setItems(filter(filter));
        }
    }

    public ArrayList<ControlVisit> filter(String text) {
        ArrayList<ControlVisit> filteredList = new ArrayList<>();
        for (ControlVisit item : visits) {
            if ((item.vehicle != null && normalize(item.vehicle.plate).contains(normalize(text)))
                    || normalize(item.visitor.dni).contains(normalize(text))
                    || normalize(item.visitor.getFullname()).contains(normalize(text))
                    || normalize(item.clerk.dni).contains(normalize(text))
                    || normalize(item.clerk.getFullname()).contains(normalize(text))){
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
