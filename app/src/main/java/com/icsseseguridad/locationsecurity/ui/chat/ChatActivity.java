package com.icsseseguridad.locationsecurity.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.ChatAdapter;
import com.icsseseguridad.locationsecurity.controller.MessengerController;
import com.icsseseguridad.locationsecurity.events.OnListMessageFailure;
import com.icsseseguridad.locationsecurity.events.OnListMessageSuccess;
import com.icsseseguridad.locationsecurity.events.OnSendMessageFailure;
import com.icsseseguridad.locationsecurity.events.OnSendMessageSuccess;
import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.ChatLine;
import com.icsseseguridad.locationsecurity.service.AppFirebaseMessagingService;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.util.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.comment_loading) View commentLoading;
    @BindView(R.id.messing_area) View messingArea;
    @BindView(R.id.new_message) EditText messageText;
    @BindView(R.id.send) View messageSend;
    @BindView(R.id.sending) View messageSending;

    private List<ChatLine> messages;
    private ChatAdapter chatAdapter;

    private Long chatId;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageString = intent.getStringExtra(Const.NEW_MESSAGE);
            final ChatLine chatLine = gson().fromJson(messageString, ChatLine.class);
            if (chatLine.chatId != null && chatId != null && chatLine.chatId.equals(chatId)) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        addLine(chatLine);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat
                                .from(ChatActivity.this);
                        notificationManager.cancel(AppFirebaseMessagingService.ID_MESSAGE);
                    }
                });
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Long chatId = intent.getLongExtra("chat_id", 0);
        if (chatId == 0) {
            Toast.makeText(this, "Chat invalido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        this.chatId = chatId;
        messages.clear();
        chatAdapter.notifyDataSetChanged();
        getMessages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        Long chatId = getIntent().getLongExtra("chat_id", 0);
        if (chatId == 0) {
            Toast.makeText(this, "Chat invalido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        this.chatId = chatId;
        getMessages();
    }

    public void getMessages()  {
        commentLoading.setVisibility(View.VISIBLE);
        new MessengerController().getMessages(chatId);
        messingArea.setVisibility(View.GONE);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListMessageFailure(OnListMessageFailure event) {
        EventBus.getDefault().removeStickyEvent(OnListMessageFailure.class);
        commentLoading.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListMessageSuccess(OnListMessageSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnListMessageSuccess.class);
        messages = event.list.messages;
        setupMessages(messages);
    }

    private void setupMessages(List<ChatLine> messages) {
        Collections.reverse(messages);
        System.out.println(gson().toJson(messages));
        this.messages = messages;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);
        commentLoading.setVisibility(View.GONE);
        messingArea.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.send)
    public void send() {
        String message = messageText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ChatLine chatLine = new ChatLine();
        chatLine.text = message;
        chatLine.chatId = chatId;
        chatLine.senderId = getPreferences().getGuard().id;
        chatLine.senderType = Chat.TYPE.GUARD;
        chatLine.senderName = getPreferences().getGuard().getFullname();

        messageText.setEnabled(false);
        messageSend.setVisibility(View.GONE);
        messageSending.setVisibility(View.VISIBLE);
        new MessengerController().send(chatLine);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void sendMessageSuccess(OnSendMessageSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnSendMessageSuccess.class);
        messageText.setEnabled(true);
        messageSend.setVisibility(View.VISIBLE);
        messageSending.setVisibility(View.GONE);
        messageText.setText("");
        addLine(event.message);
    }

    public void addLine(ChatLine message) {
        messages.add(message);
        chatAdapter.replaceAll(messages);
        recyclerView.smoothScrollToPosition(messages.indexOf(message));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void sendMessageFailure(OnSendMessageFailure event) {
        EventBus.getDefault().removeStickyEvent(OnSendMessageFailure.class);
        messageText.setEnabled(true);
        messageSend.setVisibility(View.VISIBLE);
        messageSending.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageReceiver, new IntentFilter(Const.NEW_MESSAGE));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(AppFirebaseMessagingService.ID_MESSAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }
}
