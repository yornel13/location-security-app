package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.sql.Timestamp;

import ir.mirrajabi.searchdialog.core.Searchable;

public class VisitorVehicle implements Searchable {

    @SerializedName("id")
    public Long id;

    @SerializedName("plate")
    public String plate;

    @SerializedName("vehicle")
    public String vehicle;

    @SerializedName("model")
    public String model;

    @SerializedName("type")
    public String type;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("update_date")
    public Timestamp updateDate;

    @SerializedName("photo")
    public String photo;

    @SerializedName("active")
    public Integer active;

    @SerializedName("last_visit")
    public LastVisit lastVisit;

    @Override
    public String getTitle() {
        return this.plate;
    }
}
