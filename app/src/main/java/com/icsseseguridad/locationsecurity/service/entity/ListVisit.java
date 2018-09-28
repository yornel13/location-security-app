package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListVisit {

    @SerializedName("data")
    public List<ControlVisit> visits;

    @SerializedName("total")
    public Integer total;
}
