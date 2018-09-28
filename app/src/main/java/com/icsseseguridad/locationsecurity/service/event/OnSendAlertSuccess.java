package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Alert;

public class OnSendAlertSuccess {

    public final Alert alert;

    public OnSendAlertSuccess(Alert alert) {
        this.alert = alert;
    }
}
