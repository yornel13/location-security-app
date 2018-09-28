package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import ir.mirrajabi.searchdialog.core.Searchable;

@Entity(tableName = "clerk", indices = {@Index(value = "id", unique = true)})
public class Clerk implements Searchable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "dni")
    @SerializedName("dni")
    public String dni;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;

    @ColumnInfo(name = "lastname")
    @SerializedName("lastname")
    public String lastname;

    @ColumnInfo(name = "address")
    @SerializedName("address")
    public String address;

    @ColumnInfo(name = "create_date")
    @SerializedName("create_date")
    public Timestamp createDate;

    @ColumnInfo(name = "update_date")
    @SerializedName("update_date")
    public Timestamp updateDate;

    @ColumnInfo(name = "active")
    @SerializedName("active")
    public Integer active;

    @Ignore
    public String getFullname() {
        return name+" "+lastname;
    }

    @Ignore
    @Override
    public String getTitle() {
        return this.dni;
    }
}
