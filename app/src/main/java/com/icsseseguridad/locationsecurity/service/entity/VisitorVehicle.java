package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.UUID;

import ir.mirrajabi.searchdialog.core.Searchable;

@Entity(tableName = "vehicle", indices = {@Index(value = "id", unique = true)})
public class VisitorVehicle implements Searchable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "plate")
    @SerializedName("plate")
    public String plate;

    @ColumnInfo(name = "vehicle")
    @SerializedName("vehicle")
    public String vehicle;

    @ColumnInfo(name = "model")
    @SerializedName("model")
    public String model;

    @ColumnInfo(name = "type")
    @SerializedName("type")
    public String type;

    @ColumnInfo(name = "create_date")
    @SerializedName("create_date")
    public String createDate;

    @ColumnInfo(name = "update_date")
    @SerializedName("update_date")
    public String updateDate;

    @ColumnInfo(name = "photo")
    @SerializedName("photo")
    public String photo;

    @ColumnInfo(name = "active")
    @SerializedName("active")
    public Integer active;

    @Embedded
    @SerializedName("last_visit")
    public LastVisit lastVisit;

    @ColumnInfo(name = "sync")
    public boolean sync = true;

    @Ignore
    @Override
    public String getTitle() {
        return this.plate;
    }
}
