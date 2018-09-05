package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnRegisteredTabletSuccess {

    public final MultipleResource response;

    public OnRegisteredTabletSuccess(MultipleResource response) {
        this.response = response;
    }
}
