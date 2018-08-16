package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListReply {

    @SerializedName("data")
    public List<Reply> replies;

    @SerializedName("total")
    public Integer total;
}
