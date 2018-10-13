package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListVisitor {

    @SerializedName("data")
    public List<Visitor> visitors;

    @SerializedName("total")
    public Integer total;
}
