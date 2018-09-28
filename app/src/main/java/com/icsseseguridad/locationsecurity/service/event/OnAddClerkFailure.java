package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnAddClerkFailure {

    public final MultipleResource response;

    public OnAddClerkFailure(MultipleResource response) {
        this.response = response;
    }
}
