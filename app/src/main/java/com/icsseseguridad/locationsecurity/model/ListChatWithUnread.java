package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListChatWithUnread {

    @SerializedName("data")
    public ArrayList<ChatWithUnread> chatsUnread;

    @SerializedName("unread")
    public Integer unread;
}