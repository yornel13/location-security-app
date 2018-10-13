package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import com.google.gson.annotations.SerializedName;
import com.icsseseguridad.locationsecurity.util.UTILITY;

@Entity(tableName = "position", indices = {@Index(value = "id", unique = true)})
public class TabletPosition {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    public String longitude;

    @ColumnInfo(name = "generated_time")
    @SerializedName("generated_time")
    public String generatedTime;

    @ColumnInfo(name = "message_time")
    @SerializedName("message_time")
    public String messageTime;

    @ColumnInfo(name = "watch_id")
    @SerializedName("watch_id")
    public Long watchId;

    @ColumnInfo(name = "imei")
    @SerializedName("imei")
    public String imei;

    @ColumnInfo(name = "message")
    @SerializedName("message")
    public String message;

    @ColumnInfo(name = "alert_message")
    @SerializedName("alert_message")
    public String alertMessage;

    @ColumnInfo(name = "is_exception")
    @SerializedName("is_exception")
    public boolean isException = false;

    @ColumnInfo(name = "sync")
    public boolean sync = false;

    public TabletPosition() { }

    public TabletPosition(Location location, String imei) {
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        this.messageTime = UTILITY.longToString(location.getTime());
        this.imei = imei;
        this.message = MESSAGE.UPDATE.name();
    }

    public enum MESSAGE {
        UPDATE,
        INIT_WATCH,
        RESUMED_WATCH,
        FINISHED_WATCH,
        INCIDENCE_LEVEL_1,
        INCIDENCE_LEVEL_2,
        DROP,
        SOS1
    }
}
