package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Guard;

public class OnVerifySessionSuccess {

    public final Guard guard;

    public OnVerifySessionSuccess(Guard guard) {
        this.guard = guard;
    }
}
