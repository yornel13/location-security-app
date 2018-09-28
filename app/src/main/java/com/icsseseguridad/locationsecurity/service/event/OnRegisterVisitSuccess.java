package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;

public class OnRegisterVisitSuccess {

    public final ControlVisit controlVisit;

    public OnRegisterVisitSuccess(ControlVisit controlVisit) {
        this.controlVisit = controlVisit;
    }
}
