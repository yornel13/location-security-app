package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListVisitorVehicle;

public class OnListVisitorVehicleSuccess {

    public final ListVisitorVehicle list;

    public OnListVisitorVehicleSuccess(ListVisitorVehicle list) {
        this.list = list;
    }
}
