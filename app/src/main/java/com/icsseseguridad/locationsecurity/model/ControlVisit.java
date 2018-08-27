package com.icsseseguridad.locationsecurity.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ControlVisit {

    @SerializedName("id")
    public Long id;

    @SerializedName("vehicle_id")
    public Long vehicleId;

    @SerializedName("visitor_id")
    public Long visitorId;

    @SerializedName("guard_id")
    public Long guardId;

    @SerializedName("visited_id")
    public Long clerkId;

    @SerializedName("create_date")
    public Timestamp createDate;

    @SerializedName("update_date")
    public Timestamp updateDate;

    @SerializedName("persons")
    public Integer persons;

    @SerializedName("observation")
    public String materials;

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

    @SerializedName("visitor")
    public Visitor visitor;

    @SerializedName("visited")
    public Clerk clerk;

    @SerializedName("vehicle")
    public VisitorVehicle vehicle;

    /////////////////////////////////////////////////

    private List<String> uris;

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

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
