package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListReport {

    @SerializedName("data")
    public List<SpecialReport> reports;

    @SerializedName("total")
    public Integer total;
}
