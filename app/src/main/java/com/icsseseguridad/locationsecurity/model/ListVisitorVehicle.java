package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListVisitorVehicle {

    @SerializedName("data")
    public List<VisitorVehicle> vehicles;

    @SerializedName("total")
    public Integer total;

    public ArrayList<VisitorVehicle> getArray(Boolean dialog) {
        ArrayList<VisitorVehicle> arrayVehicles = new ArrayList<>(vehicles);
        if (dialog) {
            VisitorVehicle addVehicle = new VisitorVehicle();
            addVehicle.plate = "Crear Vehiculo...";
            arrayVehicles.add(0, addVehicle);
        }
        return arrayVehicles;
    }
}
