package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListChat {

    @SerializedName("data")
    public ArrayList<Chat> chats;

    @SerializedName("total")
    public Integer total;
}
