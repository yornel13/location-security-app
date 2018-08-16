package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnFinishVisitFailure {

    public final MultipleResource response;

    public OnFinishVisitFailure(MultipleResource response) {
        this.response = response;
    }
}
