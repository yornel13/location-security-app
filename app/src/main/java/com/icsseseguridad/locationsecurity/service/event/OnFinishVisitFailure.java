package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnFinishVisitFailure {

    public final MultipleResource response;

    public OnFinishVisitFailure(MultipleResource response) {
        this.response = response;
    }
}
