package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListAdmin {

    @SerializedName("data")
    public ArrayList<Admin> admins;

    @SerializedName("total")
    public Integer total;
}
