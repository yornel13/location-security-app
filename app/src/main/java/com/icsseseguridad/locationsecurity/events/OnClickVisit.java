package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ControlVisit;

public class OnClickVisit {

    public final ControlVisit controlVisit;

    public OnClickVisit(ControlVisit controlVisit) {
        this.controlVisit = controlVisit;
    }
}
