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

@Entity(tableName = "special_report", indices = {@Index(value = "id", unique = true)})
public class SpecialReport {

    @ColumnInfo(name = "sync_id")
    @SerializedName("sync_id")
    public String syncId = UUID.randomUUID().toString();

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "incidence_id")
    @SerializedName("incidence_id")
    public Long incidenceId;

    @ColumnInfo(name = "watch_id")
    @SerializedName("watch_id")
    public Long watchId;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    public String title;

    @ColumnInfo(name = "observation")
    @SerializedName("observation")
    public String observation;

    @ColumnInfo(name = "create_date")
    @SerializedName("create_date")
    public Timestamp createDate;

    @ColumnInfo(name = "update_date")
    @SerializedName("update_date")
    public Timestamp updateDate;

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    public String longitude;

    @ColumnInfo(name = "image_1")
    @SerializedName("image_1")
    public String image1;

    @ColumnInfo(name = "image_2")
    @SerializedName("image_2")
    public String image2;

    @ColumnInfo(name = "image_3")
    @SerializedName("image_3")
    public String image3;

    @ColumnInfo(name = "image_4")
    @SerializedName("image_4")
    public String image4;

    @ColumnInfo(name = "image_5")
    @SerializedName("image_5")
    public String image5;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    public Integer status;

    @ColumnInfo(name = "resolved")
    @SerializedName("resolved")
    public Integer resolved;

    @ColumnInfo(name = "guard_id")
    @SerializedName("guard_id")
    public Long guardId;

    @ColumnInfo(name = "guard_dni")
    @SerializedName("guard_dni")
    public String guardDni;

    @ColumnInfo(name = "guard_name")
    @SerializedName("guard_name")
    public String guardName;

    @ColumnInfo(name = "guard_lastname")
    @SerializedName("guard_lastname")
    public String guardLastname;

    @ColumnInfo(name = "level")
    @SerializedName("level")
    public Integer level;

    @ColumnInfo(name = "sync")
    public boolean sync = true;

    @Ignore
    public Integer unread = 0;

    @Ignore
    public Incidence incidence;

    @Ignore
    public Watch watch;

}
