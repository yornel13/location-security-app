package com.icsseseguridad.locationsecurity.view.ui.chat;

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
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.adapter.ChatAdapter;
import com.icsseseguridad.locationsecurity.service.repository.MessengerController;
import com.icsseseguridad.locationsecurity.service.event.OnListMessageFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListMessageSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSendMessageFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSendMessageSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.ChatLine;
import com.icsseseguridad.locationsecurity.service.background.MessagingService;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.util.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    public static final String CHAT_TYPE = "chat_type";
    public static final String CHAT_ID = "chat_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHAT = "CHAT";
    public static final String CHANNEL = "CHANNEL";

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
    private Long channelId;
    private String chatType;

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
                        putAllRead(false);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat
                                .from(ChatActivity.this);
                        notificationManager.cancel(MessagingService.ID_MESSAGE);
                    }
                });
            }
            if (chatLine.channelId != null && channelId != null && chatLine.channelId.equals(channelId)) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        addLine(chatLine);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat
                                .from(ChatActivity.this);
                        notificationManager.cancel(MessagingService.ID_MESSAGE);
                    }
                });
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        messages.clear();
        chatAdapter.notifyDataSetChanged();
        setupChat(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        setupChat(getIntent());
    }

    private void setupChat(Intent intent) {
        chatId = null;
        channelId = null;
        chatType = intent.getStringExtra(CHAT_TYPE);
        if (chatType == null) {
            finish();
            System.out.println("without chat type");
        }
        if (chatType.equals(CHAT)) {
            Long chatId = intent.getLongExtra(CHAT_ID, 0);
            if (chatId == 0) {
                Toast.makeText(this, "Chat invalido", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            this.chatId = chatId;
        }
        if (chatType.equals(CHANNEL)) {
            Long channelId = intent.getLongExtra(CHANNEL_ID, 0);
            if (channelId == 0) {
                Toast.makeText(this, "Chat invalido", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            this.channelId = channelId;
        }
        getMessages();
    }

    public void getMessages()  {
        commentLoading.setVisibility(View.VISIBLE);
        if (chatType.equals(CHAT))
            new MessengerController().getMessages(chatId);
        else
            new MessengerController().getChannelMessages(channelId);
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
        order(messages);
        setupMessages(messages);
    }


    public void order(List<ChatLine> objects) {
        Collections.sort(objects, new Comparator<ChatLine>(){
            public int compare(ChatLine obj1, ChatLine obj2) {
                return UTILITY.stringToDate(obj2.createAt).compareTo(UTILITY.stringToDate(obj1.createAt));
            }
        });
    }

    private void setupMessages(List<ChatLine> messages) {
        Collections.reverse(messages);
        this.messages = messages;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);
        commentLoading.setVisibility(View.GONE);
        messingArea.setVisibility(View.VISIBLE);
        putAllRead(true);
    }

    @OnClick(R.id.send)
    public void send() {
        String message = messageText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ChatLine chatLine = new ChatLine();
        chatLine.text = message;
        if (chatType.equals(CHAT))
            chatLine.chatId = chatId;
        else
            chatLine.channelId = channelId;
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
        notificationManager.cancel(MessagingService.ID_MESSAGE);
        if (messingArea.getVisibility() == View.VISIBLE) {
            if (chatType.equals(CHAT))
                new MessengerController().getMessages(chatId);
            else
                new MessengerController().getChannelMessages(channelId);
        }
    }

    void putAllRead(boolean checkUnreadSize) {
        if (this.messages != null
                && this.messages.size() > 0
                && this.chatId != null
                && app.unreadMessages != null) {
            if (!checkUnreadSize || app.unreadMessages.unread > 0) {
                new MessengerController().putChatRead(chatId);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }
}
