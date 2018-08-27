package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListAdmin {

    @SerializedName("data")
    public ArrayList<Admin> admins;

    @SerializedName("total")
    public Integer total;
}
