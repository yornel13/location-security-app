package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListIncidence {

    @SerializedName("data")
    public List<Incidence> incidences;

    @SerializedName("total")
    public Integer total;
}
