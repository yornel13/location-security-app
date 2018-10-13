package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import ir.mirrajabi.searchdialog.core.Searchable;

public class Alert {

    @SerializedName("id")
    public Long id;

    @SerializedName("guard_id")
    public Long guardId;

    @SerializedName("imei")
    public String imei;

    @SerializedName("cause")
    public CAUSE cause;

    @SerializedName("type")
    public CAUSE type;

    @SerializedName("message")
    public String message;

    @SerializedName("extra")
    public String extra;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("update_date")
    public String updateDate;

    @SerializedName("status")
    public Integer status;

    public enum CAUSE {
        DROP, SOS1
    }
}
