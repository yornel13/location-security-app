package com.icsseseguridad.locationsecurity.model;

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

    @SerializedName("state")
    public Integer state;
}