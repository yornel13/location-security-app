package com.icsseseguridad.locationsecurity.model;

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
    public Timestamp createDate;

    @SerializedName("updateData")
    public Timestamp updateDate;

    @SerializedName("status")
    public Integer active;

    @SerializedName("resumed")
    public Boolean resumed;
}
