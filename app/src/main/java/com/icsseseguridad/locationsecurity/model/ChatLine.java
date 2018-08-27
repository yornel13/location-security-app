package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class ChatLine {

    @SerializedName("id")
    public Long id;

    @SerializedName("chat_id")
    public Long chatId;

    @SerializedName("channel_id")
    public Long channelId;

    @SerializedName("text")
    public String text;

    @SerializedName("image")
    public String image;

    @SerializedName("create_at")
    public Timestamp createAt;

    @SerializedName("sender_id")
    public Long senderId;

    @SerializedName("sender_type")
    public Chat.TYPE senderType;

    @SerializedName("sender_name")
    public String senderName;

    @SerializedName("state")
    public Integer state;

    /////////////////////////////////////
    @SerializedName("receiver_id")
    public Long receiverId;

    @SerializedName("receiver_type")
    public Chat.TYPE  receiverType;
}
