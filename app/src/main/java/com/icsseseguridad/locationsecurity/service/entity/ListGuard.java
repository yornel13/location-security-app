package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListGuard {

    @SerializedName("data")
    public ArrayList<Guard> guards;

    @SerializedName("total")
    public Integer total;
}
