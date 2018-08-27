package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListChannel {

    @SerializedName("data")
    public ArrayList<Channel> messages;

    @SerializedName("total")
    public Integer total;
}
