package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListVisitorVehicle {

    @SerializedName("data")
    public List<VisitorVehicle> vehicles;

    @SerializedName("total")
    public Integer total;
}
