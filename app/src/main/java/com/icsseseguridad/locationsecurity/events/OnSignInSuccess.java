package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Guard;

public class OnSignInSuccess {

    public final Guard guard;

    public OnSignInSuccess(Guard guard) {
        this.guard = guard;
    }
}
