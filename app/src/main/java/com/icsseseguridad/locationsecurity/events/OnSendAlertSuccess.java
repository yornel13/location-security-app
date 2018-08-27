package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Alert;

public class OnSendAlertSuccess {

    public final Alert alert;

    public OnSendAlertSuccess(Alert alert) {
        this.alert = alert;
    }
}
