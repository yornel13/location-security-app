package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnAddVisitorFailure {

    public final MultipleResource response;

    public OnAddVisitorFailure(MultipleResource response) {
        this.response = response;
    }
}
