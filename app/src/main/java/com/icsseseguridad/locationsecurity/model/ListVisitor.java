package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListVisitor {

    @SerializedName("data")
    public List<Visitor> visitors;

    @SerializedName("total")
    public Integer total;

    public ArrayList<Visitor> getArray(Boolean dialog) {
        ArrayList<Visitor> arrayVisitor = new ArrayList<>(visitors);
        if (dialog) {
            Visitor addVisitor = new Visitor();
            addVisitor.dni = "Crear Visitante...";
            arrayVisitor.add(0, addVisitor);
        }
        return arrayVisitor;
    }
}
