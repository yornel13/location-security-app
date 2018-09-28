package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListIncidence {

    @SerializedName("data")
    public List<Incidence> incidences;

    @SerializedName("total")
    public Integer total;
}
