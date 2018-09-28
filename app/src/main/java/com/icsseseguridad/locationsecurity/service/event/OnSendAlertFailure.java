package com.icsseseguridad.locationsecurity.service.event;

public class OnSendAlertFailure {

    public final String message;

    public OnSendAlertFailure(String message) {
        this.message = message;
    }
}
