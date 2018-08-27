package com.icsseseguridad.locationsecurity.ui.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.AdminModelAdapter;
import com.icsseseguridad.locationsecurity.adapter.MessengerAdapter;
import com.icsseseguridad.locationsecurity.controller.MessengerController;
import com.icsseseguridad.locationsecurity.dialog.AdminSearchDialog;
import com.icsseseguridad.locationsecurity.dialog.GuardSearchDialog;
import com.icsseseguridad.locationsecurity.events.OnClickChat;
import com.icsseseguridad.locationsecurity.events.OnCreateChannelFailure;
import com.icsseseguridad.locationsecurity.events.OnCreateChannelSuccess;
import com.icsseseguridad.locationsecurity.events.OnCreateChatFailure;
import com.icsseseguridad.locationsecurity.events.OnCreateChatSuccess;
import com.icsseseguridad.locationsecurity.events.OnListAdminFailure;
import com.icsseseguridad.locationsecurity.events.OnListAdminSuccess;
import com.icsseseguridad.locationsecurity.events.OnListChatFailure;
import com.icsseseguridad.locationsecurity.events.OnListChatSuccess;
import com.icsseseguridad.locationsecurity.events.OnListGuardFailure;
import com.icsseseguridad.locationsecurity.events.OnListGuardSuccess;
import com.icsseseguridad.locationsecurity.model.Admin;
import com.icsseseguridad.locationsecurity.model.Channel;
import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.ui.binnacle.BinnacleActivity;
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
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class MessageActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final Integer INTENT_NEW_CHAT = 11;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.fab_menu) FloatingActionsMenu fabMenu;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.empty) View emptyView;

    private MessengerAdapter adapter;

    List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        setupAdapter(new ArrayList<Chat>());
        new MessengerController().getConversations(getPreferences().getGuard().id);
        new MessengerController().getGroups(getPreferences().getGuard().id);

        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void setupAdapter(List<Chat> visits) {
        this.chats = visits;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MessengerAdapter(this, visits);
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
        nameText.setText(getPreferences().getGuard().getFullname());
        dateText.setText(UTILITY.getCurrentDate());
    }

    @OnClick(R.id.chat_guard)
    public void chatGuard() {
        fabMenu.collapse();
        new MessengerController().getGuard();
        builderDialog.text("Cargando...");
        dialog.show();
    }

    @OnClick(R.id.chat_admin)
    public void chatAdmin() {
        fabMenu.collapse();
        new MessengerController().getAdmin();
        builderDialog.text("Cargando...");
        dialog.show();
    }

    @OnClick(R.id.chat_group)
    public void chatGroup() {
        fabMenu.collapse();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewMyLayout = inflater.inflate(R.layout.name_layout, null);
        final EditText edittext = viewMyLayout.findViewById(R.id.name);
        alert.setMessage("Ingresa un nombre para el grupo");
        alert.setTitle("Crear Nuevo Grupo");
        alert.setView(viewMyLayout);
        alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.setPositiveButton("CREAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = edittext.getText().toString();
                createGroup(name);
            }
        });
        alert.show();
    }

    public void createGroup(String name) {
        if (name.isEmpty() || name.length() < 4) {
            Toast.makeText(this, "El nombre del grupo debe contener minimo 4 caracteres",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Channel channel = new Channel();
        channel.name = name;
        channel.creatorId = getPreferences().getGuard().id;
        channel.creatorType = Chat.TYPE.GUARD;
        channel.creatorName = getPreferences().getGuard().getFullname();
        new MessengerController().channel(channel);
        builderDialog.text("Cargando...");
        dialog.show();
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
        new GuardSearchDialog<>(this, "Selecciona un Guardia",
                "Buscar guardia...", null, event.list.guards,
                new SearchResultListener<Guard>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog,
                                           Guard item, int position) {
                        createChat(item);
                        dialog.dismiss();
                    }
                }).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListAdminFailure(OnListAdminFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListAdminFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListAdminSuccess(OnListAdminSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListAdminSuccess.class);
        dialog.dismiss();
        new AdminSearchDialog<>(this, "Selecciona un Administrador",
                "Buscar administrador...", null, event.list.admins,
                new SearchResultListener<Admin>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog,
                                           Admin item, int position) {
                        createChat(item);
                        dialog.dismiss();
                    }
                }).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListGuardFailure(OnListGuardFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListGuardFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    public void createChat(Guard guard) {
        if (chats != null) {
            for (Chat chat: chats) {
                if ((chat.user1Id.equals(guard.id) && chat.user1Type == Chat.TYPE.GUARD) ||
                        (chat.user2Id.equals(guard.id) && chat.user2Type == Chat.TYPE.GUARD)) {
                    onClickChat(new OnClickChat(chat));
                    return;
                }
            }
        }
        Chat chat = new Chat();
        chat.user1Id = getPreferences().getGuard().id;
        chat.user1Type = Chat.TYPE.GUARD;
        chat.user1Name = getPreferences().getGuard().getFullname();
        chat.user2Id = guard.id;
        chat.user2Type = Chat.TYPE.GUARD;
        chat.user2Name = guard.getFullname();
        new MessengerController().chat(chat);
        builderDialog.text("Creando...");
        dialog.show();
    }

    public void createChat(Admin admin) {
        if (chats != null) {
            for (Chat chat: chats) {
                if ((chat.user1Id.equals(admin.id) && chat.user1Type == Chat.TYPE.ADMIN) ||
                        (chat.user2Id.equals(admin.id) && chat.user2Type == Chat.TYPE.ADMIN)) {
                    onClickChat(new OnClickChat(chat));
                    return;
                }
            }
        }
        Chat chat = new Chat();
        chat.user1Id = getPreferences().getGuard().id;
        chat.user1Type = Chat.TYPE.GUARD;
        chat.user1Name = getPreferences().getGuard().getFullname();
        chat.user2Id = admin.id;
        chat.user2Type = Chat.TYPE.ADMIN;
        chat.user2Name = admin.getFullname();
        new MessengerController().chat(chat);
        builderDialog.text("Creando...");
        dialog.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreateChatSuccess(OnCreateChatSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnCreateChatSuccess.class);
        dialog.dismiss();
        chats.add(0, event.chat);
        adapter.setItems(chats);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat_id", event.chat.id);
        startActivityForResult(intent, INTENT_NEW_CHAT);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreateChatFailure(OnCreateChatFailure event) {
        EventBus.getDefault().removeStickyEvent(OnCreateChatFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListChatFailure(OnListChatFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListChatFailure.class);
        loadingView.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListChatSuccess(OnListChatSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListChatSuccess.class);
        chats = event.list.chats;
        checkView();
    }

    public void checkView() {
        loadingView.setVisibility(View.GONE);
        if (chats.size() > 0) {
            emptyView.setVisibility(View.GONE);
            adapter.replaceAll(chats);
        } else {
            adapter.replaceAll(new ArrayList<Chat>());
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickChat(OnClickChat event) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat_id", event.chat.id);
        startActivityForResult(intent, INTENT_NEW_CHAT);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreateChannelSuccess(OnCreateChannelSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnCreateChannelSuccess.class);
        dialog.dismiss();
        Snackbar.make(toolbar, "Grupo Creado!", Snackbar.LENGTH_LONG).show();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreateChannelFailure(OnCreateChannelFailure event) {
        EventBus.getDefault().removeStickyEvent(OnCreateChannelFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}
