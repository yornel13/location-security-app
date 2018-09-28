package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnPostPositionFailure {

    public final MultipleResource response;

    public OnPostPositionFailure(MultipleResource response) {
        this.response = response;
    }
}
