package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import ir.mirrajabi.searchdialog.core.Searchable;

public class Admin implements Searchable {

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
    public String createDate;

    @SerializedName("update_date")
    public String updateDate;

    @SerializedName("active")
    public Boolean active;

    @SerializedName("photo")
    public String photo;

    @SerializedName("token")
    public String token;

    public boolean check = false;

    public String getFullname() {
        return name+" "+lastname;
    }

    @Override
    public String getTitle() {
        return this.dni;
    }
}
