package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnAddVehicleFailure {

    public final MultipleResource response;

    public OnAddVehicleFailure(MultipleResource response) {
        this.response = response;
    }
}
