package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

public class Company {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    public Company(String name) {
        this.name = name;
    }
}
