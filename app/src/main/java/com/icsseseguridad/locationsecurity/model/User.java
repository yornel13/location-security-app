package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    public Long user_id;

    @SerializedName("user_type")
    public String user_type;

    @SerializedName("user_name")
    public String user_name;
}
