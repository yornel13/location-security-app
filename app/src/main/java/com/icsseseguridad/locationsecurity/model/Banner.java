package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Banner {

    @SerializedName("id")
    public Long id;

    @SerializedName("photo")
    public String photo;

    @SerializedName("create_at")
    public Timestamp createAt;
}
