package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "banner", indices = {@Index(value = "id", unique = true)})
public class Banner {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "photo")
    @SerializedName("photo")
    public String photo;

    @ColumnInfo(name = "create_at")
    @SerializedName("create_at")
    public Timestamp createAt;
}
