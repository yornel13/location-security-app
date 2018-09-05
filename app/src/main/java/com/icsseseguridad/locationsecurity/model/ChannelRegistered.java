package com.icsseseguridad.locationsecurity.model;

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
    public Timestamp createAt;

    @SerializedName("update_at")
    public Timestamp updateAt;

    @SerializedName("state")
    public Integer state;


    /****    Channel data       *****/
    @SerializedName("channel_id")
    public Long channelId;

    @SerializedName("channel_name")
    public String channelName;

    @SerializedName("channel_creator_id")
    public Long channelCreatorId;

    @SerializedName("channel_creator_type")
    public Chat.TYPE channelCreatorType;

    @SerializedName("channel_creator_name")
    public String channelCreatorName;

    @SerializedName("channel_create_at")
    public Timestamp channelCreateAt;

    @SerializedName("channel_update_at")
    public Timestamp channelUpdateAt;

    @SerializedName("channel_state")
    public Integer channelState;

}
