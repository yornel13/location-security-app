package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Chat {

    @SerializedName("id")
    public Long id;

    @SerializedName("user_1_id")
    public Long user1Id;

    @SerializedName("user_1_type")
    public TYPE user1Type;

    @SerializedName("user_1_name")
    public String user1Name;

    @SerializedName("user_2_id")
    public Long user2Id;

    @SerializedName("user_2_type")
    public TYPE user2Type;

    @SerializedName("user_2_name")
    public String user2Name;

    @SerializedName("create_at")
    public String createAt;

    @SerializedName("update_at")
    public String updateAt;

    @SerializedName("state")
    public Integer state;

    public Integer unread;

    public enum TYPE {
        GUARD, ADMIN
    }
}
