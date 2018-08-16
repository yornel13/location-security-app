package com.icsseseguridad.locationsecurity.model;

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
    public Timestamp createDate;

    @SerializedName("updateData")
    public Timestamp updateDate;

    @SerializedName("active")
    public Boolean active;
}
