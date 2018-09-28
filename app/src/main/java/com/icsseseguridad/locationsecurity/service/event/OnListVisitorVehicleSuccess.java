package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListVisitorVehicle;

public class OnListVisitorVehicleSuccess {

    public final ListVisitorVehicle list;

    public OnListVisitorVehicleSuccess(ListVisitorVehicle list) {
        this.list = list;
    }
}
