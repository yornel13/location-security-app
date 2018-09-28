package com.icsseseguridad.locationsecurity.service.event;

public class OnVerifySessionFailure {

    public final String response;

    public OnVerifySessionFailure(String response) {
        this.response = response;
    }
}
