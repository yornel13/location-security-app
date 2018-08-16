package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnRegisterVisitFailure {

    public final MultipleResource response;

    public OnRegisterVisitFailure(MultipleResource response) {
        this.response = response;
    }
}
