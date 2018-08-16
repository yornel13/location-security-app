package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ControlVisit;

public class OnRegisterVisitSuccess {

    public final ControlVisit controlVisit;

    public OnRegisterVisitSuccess(ControlVisit controlVisit) {
        this.controlVisit = controlVisit;
    }
}
