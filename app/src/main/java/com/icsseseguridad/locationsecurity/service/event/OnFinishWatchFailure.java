package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnFinishWatchFailure {

    public final MultipleResource response;

    public OnFinishWatchFailure(MultipleResource response) {
        this.response = response;
    }
}
