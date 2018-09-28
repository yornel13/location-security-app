package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnRegisterReportFailure {

    public final MultipleResource response;

    public OnRegisterReportFailure(MultipleResource response) {
        this.response = response;
    }
}
