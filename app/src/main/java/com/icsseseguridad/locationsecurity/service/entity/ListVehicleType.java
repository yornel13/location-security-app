package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListVehicleType {

    @SerializedName("data")
    public List<VehicleType> types;

    @SerializedName("total")
    public Integer total;


}
