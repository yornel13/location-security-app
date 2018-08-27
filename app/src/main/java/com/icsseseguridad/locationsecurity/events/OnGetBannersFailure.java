package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnGetBannersFailure {

    public final String response;

    public OnGetBannersFailure(String response) {
        this.response = response;
    }
}
