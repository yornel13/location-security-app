package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

public class Tablet {

    @SerializedName("id")
    public Long id;

    @SerializedName("imei")
    public String imei;

    @SerializedName("alias")
    public String alias;
}
