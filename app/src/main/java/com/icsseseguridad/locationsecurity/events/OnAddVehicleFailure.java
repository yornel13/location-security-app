package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnAddVehicleFailure {

    public final MultipleResource response;

    public OnAddVehicleFailure(MultipleResource response) {
        this.response = response;
    }
}
