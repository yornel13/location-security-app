package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCompany {

    @SerializedName("data")
    public List<Company> companies;

    @SerializedName("total")
    public Integer total;


}
