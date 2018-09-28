package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Guard;

public class OnSignInSuccess {

    public final Guard guard;

    public OnSignInSuccess(Guard guard) {
        this.guard = guard;
    }
}
