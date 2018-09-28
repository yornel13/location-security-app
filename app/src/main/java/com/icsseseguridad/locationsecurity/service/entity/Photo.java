package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "photo", indices = {@Index(value = "id", unique = true)})
public class Photo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "url")
    @SerializedName("url")
    public String url;

    @ColumnInfo(name = "uri")
    @SerializedName("uri")
    public String uri;

    @ColumnInfo(name = "linked_id")
    @SerializedName("linked_id")
    public Long linkedId;

    @ColumnInfo(name = "linked_type")
    @SerializedName("linked_type")
    public LINKED linkedType;

    @ColumnInfo(name = "sync")
    public boolean sync = true;

    public Photo() { }

    @Ignore
    public Photo(String uri, LINKED linkedType, Long linkedId) {
        this.linkedType = linkedType;
        this.linkedId = linkedId;
        this.sync = false;
        this.uri = uri;
    }

    public enum LINKED {
        VEHICLE, VISITOR, MATERIAL, REPORT,
    }
}
