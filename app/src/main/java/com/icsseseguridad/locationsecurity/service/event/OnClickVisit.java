package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;

public class OnClickVisit {

    public final ControlVisit controlVisit;

    public OnClickVisit(ControlVisit controlVisit) {
        this.controlVisit = controlVisit;
    }
}
