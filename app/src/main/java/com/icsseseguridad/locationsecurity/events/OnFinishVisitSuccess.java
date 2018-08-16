package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnFinishVisitSuccess {

    public final MultipleResource response;

    public OnFinishVisitSuccess(MultipleResource response) {
        this.response = response;
    }
}
