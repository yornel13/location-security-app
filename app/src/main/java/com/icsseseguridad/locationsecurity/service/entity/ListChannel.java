package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListChannel {

    @SerializedName("data")
    public ArrayList<ChannelRegistered> channels;

    @SerializedName("total")
    public Integer total;
}
