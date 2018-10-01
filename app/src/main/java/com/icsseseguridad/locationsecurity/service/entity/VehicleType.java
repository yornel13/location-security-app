package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "vehicle_type", indices = {@Index(value = "id", unique = true)})
public class VehicleType {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;

    public VehicleType(String name) {
        this.name = name;
    }
}
