package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListBanner {

    @SerializedName("data")
    public ArrayList<Banner> banners;

    @SerializedName("total")
    public Integer total;
}
