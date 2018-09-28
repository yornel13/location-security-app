package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Channel {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("creator_id")
    public Long creatorId;

    @SerializedName("creator_type")
    public Chat.TYPE creatorType;

    @SerializedName("creator_name")
    public String creatorName;

    @SerializedName("create_at")
    public Timestamp createAt;

    @SerializedName("update_at")
    public Timestamp updateAt;

    @SerializedName("state")
    public Integer state;
}
