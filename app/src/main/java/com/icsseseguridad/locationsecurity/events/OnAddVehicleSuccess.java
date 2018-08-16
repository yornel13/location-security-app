package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.VisitorVehicle;

public class OnAddVehicleSuccess {

    public final VisitorVehicle vehicle;

    public OnAddVehicleSuccess(VisitorVehicle vehicle) {
        this.vehicle = vehicle;
    }
}
