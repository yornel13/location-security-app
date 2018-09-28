package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnRegisteredTabletSuccess {

    public final MultipleResource response;

    public OnRegisteredTabletSuccess(MultipleResource response) {
        this.response = response;
    }
}
