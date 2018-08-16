package com.icsseseguridad.locationsecurity.ui.chat;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.new_message) EditText messageText;
    @BindView(R.id.send) View messageSend;
    @BindView(R.id.sending) View messageSending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
    }

    @OnClick(R.id.send)
    public void send() {
        String message = messageText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        messageText.setText("");
        //mSocket.emit("new message", message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
