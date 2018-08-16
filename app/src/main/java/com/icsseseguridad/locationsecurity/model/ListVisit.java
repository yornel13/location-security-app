package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListVisit {

    @SerializedName("data")
    public List<ControlVisit> visits;

    @SerializedName("total")
    public Integer total;
}
