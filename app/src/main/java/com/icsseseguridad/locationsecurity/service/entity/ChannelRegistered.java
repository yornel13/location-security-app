package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class ChannelRegistered {

    @SerializedName("id")
    public Long id;

    @SerializedName("user_id")
    public Long userId;

    @SerializedName("user_type")
    public Chat.TYPE userType;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("create_at")
    public String createAt;

    @SerializedName("update_at")
    public String updateAt;

    @SerializedName("state")
    public Integer state;

    @SerializedName("channel_id")
    public Long channelId;

    public Integer unread;

    @SerializedName("channel")
    public Channel channel;
}
