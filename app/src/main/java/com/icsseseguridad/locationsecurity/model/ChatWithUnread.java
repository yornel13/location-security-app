package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

public class ChatWithUnread {

    @SerializedName("chat")
    public Chat chat;

    @SerializedName("unread")
    public Integer unread;
}
