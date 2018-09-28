package com.icsseseguridad.locationsecurity.view.ui.chat;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.view.adapter.UserAdapter;
import com.icsseseguridad.locationsecurity.service.repository.MessengerController;
import com.icsseseguridad.locationsecurity.service.event.OnAddUsersToChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnAddUsersToChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnUserCheck;
import com.icsseseguridad.locationsecurity.service.entity.Admin;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.User;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddUsersActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;

    private List<Object> users;
    private UserAdapter adapter;
    private Long channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);

        channelId = getIntent().getLongExtra(ChatActivity.CHANNEL_ID, 0);
        if (channelId == 0) {
            Toast.makeText(this, "Grupo invalido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        users = new ArrayList<>();
        setupAdapter();
        new MessengerController().getAdmin();
        new MessengerController().getGuard();
    }

    private void setupAdapter() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new UserAdapter(this, users);
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListGuardSuccess(OnListGuardSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListGuardSuccess.class);
        dialog.dismiss();
        Guard me = null;
        for (Guard guard: event.list.guards) {
            if (guard.id.equals(getPreferences().getGuard().id)) {
                me = guard;
            }
        }
        if (me != null)
            event.list.guards.remove(me);
        users.addAll(event.list.guards);
        checkView();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListAdminFailure(OnListAdminFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListAdminFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListAdminSuccess(OnListAdminSuccess event) {
        System.out.println("llego la respuesta");
        EventBus.getDefault().removeStickyEvent(OnListAdminSuccess.class);
        dialog.dismiss();
        users.addAll(event.list.admins);
        checkView();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListGuardFailure(OnListGuardFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListGuardFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    public void checkView() {
        //loadingView.setVisibility(View.GONE);
        if (users.size() > 0) {
            //emptyView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            adapter.replaceAll(new ArrayList<>());
            //emptyView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.save_button)
    public void save() {
        List<User> list = new ArrayList<>();
        for (Object object: users) {
            User user = new User();
            if (object instanceof Guard) {
                Guard guard = (Guard) object;
                user.user_id = guard.id;
                user.user_type = Chat.TYPE.GUARD.name();
                user.user_name = guard.getFullname();
                if (guard.check) {
                    list.add(user);
                }
            } else {
                Admin admin = (Admin) object;
                user.user_id = admin.id;
                user.user_type = Chat.TYPE.ADMIN.name();
                user.user_name = admin.getFullname();
                if (admin.check) {
                    list.add(user);
                }
            }
        }
        System.out.println(gson().toJson(list));
        builderDialog.text("Agregando...");
        dialog.show();
        new MessengerController().addToChannel(list, channelId);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddUsersToChannelSuccess(OnAddUsersToChannelSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnAddUsersToChannelSuccess.class);
        dialog.dismiss();
        Toast.makeText(this, "Usuarios agregados con exito!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddUsersToChannelFailure(OnAddUsersToChannelFailure event) {
        EventBus.getDefault().removeStickyEvent(OnAddUsersToChannelFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.response, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCheck(OnUserCheck event) {
        Object object = users.get(event.position);
        if (object instanceof Guard) {
            Guard guard = (Guard) object;
            guard.check = event.checked;
        }
        if (object instanceof Admin) {
            Admin admin = (Admin) object;
            admin.check = event.checked;
        }
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}
