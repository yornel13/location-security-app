package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnPostPositionFailure {

    public final MultipleResource response;

    public OnPostPositionFailure(MultipleResource response) {
        this.response = response;
    }
}
