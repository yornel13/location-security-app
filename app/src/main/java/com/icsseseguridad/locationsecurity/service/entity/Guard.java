package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.sql.Date;

import ir.mirrajabi.searchdialog.core.Searchable;

public class Guard implements Searchable {

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

    @SerializedName("update_date")
    public Timestamp updateDate;

    @SerializedName("active")
    public Boolean active;

    @SerializedName("token")
    public String token;

    @SerializedName("photo")
    public String photo;

    @SerializedName("resumed")
    public boolean resumed;

    public boolean check = false;

    public String getFullname() {
        return name+" "+lastname;
    }

    @Override
    public String getTitle() {
        return this.dni;
    }
}
