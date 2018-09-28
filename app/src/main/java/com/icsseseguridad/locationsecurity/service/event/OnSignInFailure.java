package com.icsseseguridad.locationsecurity.service.event;

public class OnSignInFailure {

    public final String message;

    public OnSignInFailure(String message) {
        this.message = message;
    }
}
