package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Guard;

public class OnVerifySessionSuccess {

    public final Guard guard;

    public OnVerifySessionSuccess(Guard guard) {
        this.guard = guard;
    }
}
