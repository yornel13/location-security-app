package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnAddVisitorFailure {

    public final MultipleResource response;

    public OnAddVisitorFailure(MultipleResource response) {
        this.response = response;
    }
}
