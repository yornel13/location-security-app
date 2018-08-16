package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

public class Incidence {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("level")
    public int level;
}
