package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.sql.Date;

public class Guard {

    @SerializedName("id")
    public Long id;

    @SerializedName("dni")
    public String dni;

    @SerializedName("name")
    public String name;

    @SerializedName("lastname")
    public String lastname;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("updateData")
    public Timestamp updateDate;

    @SerializedName("active")
    public Boolean active;

    @SerializedName("token")
    public String token;

    @SerializedName("resumed")
    public boolean resumed;

    public String getFullname() {
        return name+" "+lastname;
    }
}
