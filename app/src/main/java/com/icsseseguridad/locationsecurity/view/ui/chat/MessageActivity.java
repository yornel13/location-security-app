package com.icsseseguridad.locationsecurity.view.ui.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.view.adapter.MessengerAdapter;
import com.icsseseguridad.locationsecurity.service.repository.MessengerController;
import com.icsseseguridad.locationsecurity.view.dialog.AdminSearchDialog;
import com.icsseseguridad.locationsecurity.view.dialog.GuardSearchDialog;
import com.icsseseguridad.locationsecurity.service.event.OnClickChannel;
import com.icsseseguridad.locationsecurity.service.event.OnClickChannelAdd;
import com.icsseseguridad.locationsecurity.service.event.OnClickChat;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChatFailure;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChatSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListChatFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListChatSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadMessages;
import com.icsseseguridad.locationsecurity.service.event.OnSyncUnreadReplies;
import com.icsseseguridad.locationsecurity.service.entity.Admin;
import com.icsseseguridad.locationsecurity.service.entity.Channel;
import com.icsseseguridad.locationsecurity.service.entity.ChannelRegistered;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.ChatWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.view.ui.AlertsActivity;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.view.ui.binnacle.BinnacleActivity;
import com.icsseseguridad.locationsecurity.view.ui.visit.VisitsActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import q.rorbin.badgeview.QBadgeView;

public class MessageActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final Integer INTENT_NEW_CHAT = 11;
    public static final Integer INTENT_ADD_USERS = 12;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.guard_name) TextView nameText;
    @BindView(R.id.guard_date) TextView dateText;
    @BindView(R.id.fab_menu) FloatingActionsMenu fabMenu;
    @BindView(R.id.header_container) BottomNavigationView bottomNavigationView;

    @BindView(R.id.loading) View loadingView;
    @BindView(R.id.empty) View emptyView;

    private MessengerAdapter adapter;
    private QBadgeView badgeChat;
    private QBadgeView badgeBinnacle;

    List<Object> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        chats = new ArrayList<>();
        setupAdapter();

        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        badgeChat = new QBadgeView(this);
        badgeChat.bindTarget(bottomNavigationMenuView.getChildAt(3));

        badgeBinnacle = new QBadgeView(this);
        badgeBinnacle.bindTarget(bottomNavigationMenuView.getChildAt(1));
    }

    private void setupAdapter() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MessengerAdapter(this, chats);
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
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        new MessengerController().getConversations(getPreferences().getGuard().id);
        new MessengerController().getGroups(getPreferences().getGuard().id);

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
            for (Object object: chats) {
                if (object instanceof Chat) {
                    Chat chat = (Chat) object;
                    if ((chat.user1Id.equals(guard.id) && chat.user1Type == Chat.TYPE.GUARD) ||
                            (chat.user2Id.equals(guard.id) && chat.user2Type == Chat.TYPE.GUARD)) {
                        onClickChat(new OnClickChat(chat));
                        return;
                    }
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
            for (Object object: chats) {
                if (object instanceof Chat) {
                    Chat chat = (Chat) object;
                    if ((chat.user1Id.equals(admin.id) && chat.user1Type == Chat.TYPE.ADMIN) ||
                            (chat.user2Id.equals(admin.id) && chat.user2Type == Chat.TYPE.ADMIN)) {
                        onClickChat(new OnClickChat(chat));
                        return;
                    }
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
        onClickChat(new OnClickChat(event.chat));
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
        List<Object> remove = new ArrayList<>();
        for (Object obj: chats) {
            if (obj instanceof Chat)
                remove.add(obj);
        }
        chats.removeAll(remove);
        chats.addAll(event.list.chats);
        checkView();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListChannelFailure(OnListChannelFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListChannelFailure.class);
        loadingView.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListChannelSuccess(OnListChannelSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListChannelSuccess.class);
        List<Object> remove = new ArrayList<>();
        for (Object obj: chats) {
            if (obj instanceof ChannelRegistered)
                remove.add(obj);
        }
        chats.removeAll(remove);
        chats.addAll(event.list.channels);
        checkView();
    }

    public void checkView() {
        loadingView.setVisibility(View.GONE);
        if (chats.size() > 0) {
            emptyView.setVisibility(View.GONE);
            orderList();
            checkUnread();
            adapter.replaceAll(chats);
        } else {
            adapter.replaceAll(new ArrayList<>());
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void orderList() {
        Collections.sort(chats, new Comparator<Object>(){
            public int compare(Object obj1, Object obj2) {
                return getUpdateTime(obj2).compareTo(getUpdateTime(obj1));
            }
        });
    }

    public Timestamp getUpdateTime(Object obj) {
        if (obj instanceof Chat) {
            Chat chat = (Chat) obj;
            return chat.updateAt;
        } else {
            ChannelRegistered channel = (ChannelRegistered) obj;
            return channel.channelUpdateAt;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickChat(OnClickChat event) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_TYPE, ChatActivity.CHAT);
        intent.putExtra(ChatActivity.CHAT_ID, event.chat.id);
        startActivityForResult(intent, INTENT_NEW_CHAT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickChannel(OnClickChannel event) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_TYPE, ChatActivity.CHANNEL);
        intent.putExtra(ChatActivity.CHANNEL_ID, event.channel.channelId);
        startActivityForResult(intent, INTENT_NEW_CHAT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnClickChannelAdd(OnClickChannelAdd event) {
        Intent intent = new Intent(this, AddUsersActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_ID, event.channel.channelId);
        startActivityForResult(intent, INTENT_ADD_USERS);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreateChannelSuccess(OnCreateChannelSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnCreateChannelSuccess.class);
        dialog.dismiss();
        Snackbar.make(toolbar, "Grupo Creado!", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(this, AddUsersActivity.class);
        intent.putExtra(ChatActivity.CHANNEL_ID, event.channel.id);
        startActivityForResult(intent, INTENT_ADD_USERS);
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

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    void checkUnread() {
        for (Object ch: chats) {
            if (ch instanceof Chat) {
                Chat chat = (Chat) ch;
                chat.unread = 0;
                for (ChatWithUnread chatWithUnread : app.unreadMessages.chatsUnread) {
                    if (chat.id.longValue() == chatWithUnread.chat.id.longValue()) {
                        chat.unread = chatWithUnread.unread;
                    }
                }
            }
        }
    }
}
