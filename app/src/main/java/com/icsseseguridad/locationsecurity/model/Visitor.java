package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.sql.Timestamp;

import ir.mirrajabi.searchdialog.core.Searchable;

public class Visitor implements Searchable {

    @SerializedName("id")
    public Long id;

    @SerializedName("dni")
    public String dni;

    @SerializedName("name")
    public String name;

    @SerializedName("lastname")
    public String lastname;

    @SerializedName("company")
    public String company;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("updateData")
    public Timestamp updateDate;

    @SerializedName("photo")
    public String photo;

    @SerializedName("active")
    public Integer active;

    public String getFullname() {
        return name+" "+lastname;
    }

    @Override
    public String getTitle() {
        return this.dni;
    }
}
