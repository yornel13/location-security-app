package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ListGuard {

    @SerializedName("data")
    public ArrayList<Guard> guards;

    @SerializedName("total")
    public Integer total;
}
