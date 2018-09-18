package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Guard;

public class OnVerifySessionFailure {

    public final String response;

    public OnVerifySessionFailure(String response) {
        this.response = response;
    }
}
