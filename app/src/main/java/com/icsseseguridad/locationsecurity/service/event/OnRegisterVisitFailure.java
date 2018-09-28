package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnRegisterVisitFailure {

    public final MultipleResource response;

    public OnRegisterVisitFailure(MultipleResource response) {
        this.response = response;
    }
}
