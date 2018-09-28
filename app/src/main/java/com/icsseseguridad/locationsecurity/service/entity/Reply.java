package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Reply {

    @SerializedName("id")
    public Long id;

    @SerializedName("report_id")
    public Long reportId;

    @SerializedName("admin_id")
    public Long adminId;

    @SerializedName("guard_id")
    public Long guardId;

    @SerializedName("text")
    public String text;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("guard")
    public Guard guard;

    @SerializedName("admin")
    public Admin admin;
}
