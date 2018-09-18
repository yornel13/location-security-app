package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class SpecialReport {

    @SerializedName("id")
    public Long id;

    @SerializedName("incidence_id")
    public Long incidenceId;

    @SerializedName("watch_id")
    public Long watchId;

    @SerializedName("title")
    public String title;

    @SerializedName("observation")
    public String observation;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("updateData")
    public Timestamp updateDate;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("image_1")
    public String image1;

    @SerializedName("image_2")
    public String image2;

    @SerializedName("image_3")
    public String image3;

    @SerializedName("image_4")
    public String image4;

    @SerializedName("image_5")
    public String image5;

    @SerializedName("status")
    public Integer status;

    @SerializedName("resolved")
    public Integer resolved;

    public Integer unread;

    public Incidence incidence;

    public Watch watch;

}
