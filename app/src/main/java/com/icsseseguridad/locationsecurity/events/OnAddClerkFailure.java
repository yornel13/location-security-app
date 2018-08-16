package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnAddClerkFailure {

    public final MultipleResource response;

    public OnAddClerkFailure(MultipleResource response) {
        this.response = response;
    }
}
