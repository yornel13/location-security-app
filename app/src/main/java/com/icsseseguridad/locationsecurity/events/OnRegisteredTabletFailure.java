package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnRegisteredTabletFailure {

    public final String response;

    public OnRegisteredTabletFailure(String response) {
        this.response = response;
    }
}
