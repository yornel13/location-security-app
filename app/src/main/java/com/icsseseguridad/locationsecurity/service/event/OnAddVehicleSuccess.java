package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;

public class OnAddVehicleSuccess {

    public final VisitorVehicle vehicle;

    public OnAddVehicleSuccess(VisitorVehicle vehicle) {
        this.vehicle = vehicle;
    }
}
