package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListClerk {

    @SerializedName("data")
    public List<Clerk> clerks;

    @SerializedName("total")
    public Integer total;

    public ArrayList<Clerk> getArray(Boolean dialog) {
        ArrayList<Clerk> arrayClerk = new ArrayList<>(clerks);
        if (dialog) {
            Clerk addClerk = new Clerk();
            addClerk.dni = "Crear Funcionario...";
            arrayClerk.add(0, addClerk);
        }
        return arrayClerk;
    }
}
