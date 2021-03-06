package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListChatLine {

    @SerializedName("data")
    public ArrayList<ChatLine> messages;

    @SerializedName("total")
    public Integer total;
}
