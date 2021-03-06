package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class ConfigUtility {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public String value;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("update_date")
    public String updateDate;

    @SerializedName("active")
    public Boolean active;
}
