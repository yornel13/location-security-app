package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnFinishVisitSuccess {

    public final MultipleResource response;

    public OnFinishVisitSuccess(MultipleResource response) {
        this.response = response;
    }
}
