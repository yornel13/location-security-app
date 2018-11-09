package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.sql.Timestamp;

public class Watch {

    @SerializedName("id")
    public Long id;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("guard_id")
    public Long guardId;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("update_date")
    public String updateDate;

    @SerializedName("stand_name")
    public String standName;

    @SerializedName("status")
    public Integer active;

    @SerializedName("resumed")
    public Boolean resumed;
}
