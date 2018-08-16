package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnRegisterReportFailure {

    public final MultipleResource response;

    public OnRegisterReportFailure(MultipleResource response) {
        this.response = response;
    }
}
