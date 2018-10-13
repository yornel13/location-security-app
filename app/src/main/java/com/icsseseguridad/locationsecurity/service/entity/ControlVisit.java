package com.icsseseguridad.locationsecurity.service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(
        tableName = "control_visit",
        indices = {
                @Index(value = "id", unique = true),
                @Index("vehicle_id"),
                @Index("visitor_id"),
                @Index("clerk_id"),
                @Index("guard_id"),
                @Index("guard_out_id"),
        }
//        foreignKeys = {
//                @ForeignKey(
//                        entity = VisitorVehicle.class,
//                        parentColumns = "id",
//                        childColumns = "vehicle_id"
//                ),
//                @ForeignKey(
//                        entity = Visitor.class,
//                        parentColumns = "id",
//                        childColumns = "visitor_id"
//                ),
//                @ForeignKey(
//                        entity = Clerk.class,
//                        parentColumns = "id",
//                        childColumns = "clerk_id"
//                )
//        }
)
public class ControlVisit {

    @ColumnInfo(name = "sync_id")
    @SerializedName("sync_id")
    public String syncId = UUID.randomUUID().toString();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public Long id;

    @ColumnInfo(name = "vehicle_id")
    @SerializedName("vehicle_id")
    public Long vehicleId;

    @ColumnInfo(name = "visitor_id")
    @SerializedName("visitor_id")
    public Long visitorId;

    @ColumnInfo(name = "guard_id")
    @SerializedName("guard_id")
    public Long guardId;

    @ColumnInfo(name = "clerk_id")
    @SerializedName("visited_id")
    public Long clerkId;

    @ColumnInfo(name = "dni")
    @SerializedName("create_date")
    public String createDate;

    @ColumnInfo(name = "finish_date")
    @SerializedName("finish_date")
    public String finishDate;

    @ColumnInfo(name = "persons")
    @SerializedName("persons")
    public Integer persons;

    @ColumnInfo(name = "observation")
    @SerializedName("observation")
    public String materials;

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

    @ColumnInfo(name = "comment")
    @SerializedName("comment")
    public String comment;

    @ColumnInfo(name = "guard_out_id")
    @SerializedName("guard_out_id")
    public Long guardOutId;

    @ColumnInfo(name = "f_latitude")
    @SerializedName("f_latitude")
    public String fLatitude;

    @ColumnInfo(name = "f_longitude")
    @SerializedName("f_longitude")
    public String fLongitude;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    public Integer status;

    @ColumnInfo(name = "sync")
    public boolean sync = true;

    ////////////////////////////////////////////////

    @Embedded(prefix = "vehicle")
    @SerializedName("vehicle")
    public VisitorVehicle vehicle;

    @Embedded(prefix = "visitor")
    @SerializedName("visitor")
    public Visitor visitor;

    @Embedded(prefix = "clerk")
    @SerializedName("visited")
    public Clerk clerk;

    /////////////////////////////////////////////////

    @Ignore
    private List<String> uris;

    @Ignore
    public List<String> getUris() {
        return uris;
    }

    @Ignore
    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    @Ignore
    public List<String> getMaterialList() {
        if (materials != null) {
            List<String> list = new Gson().fromJson(materials,
                    new TypeToken<ArrayList<String>>() {
                    }.getType());
            return list;
        } else {
            return null;
        }
    }
}
