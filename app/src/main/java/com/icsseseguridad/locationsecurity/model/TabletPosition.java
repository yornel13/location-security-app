package com.icsseseguridad.locationsecurity.model;

import android.location.Location;
import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Calendar;

public class TabletPosition {

    @SerializedName("id")
    public Long id;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("generated_time")
    public Timestamp generatedTime;

    @SerializedName("message_time")
    public Timestamp messageTime;

    @SerializedName("watch_id")
    public Long watchId;

    @SerializedName("imei")
    public String imei;

    @SerializedName("message")
    public String message;

    public TabletPosition(Location location, String imei) {
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        this.messageTime = new Timestamp(location.getTime());
        this.imei = imei;
        this.message = MESSAGE.UPDATE.name();
    }

    public String getMessageTimeString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(messageTime.getTime());
        String date = DateFormat.format("yyyy-MM-dd  HH:mm:ss", cal).toString();
        return date;
    }

    public enum MESSAGE {
        UPDATE, INIT_WATCH, RESUMED_WATCH, FINISHED_WATCH, INCIDENCE
    }
}
