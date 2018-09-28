package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnFinishWatchSuccess {

    public final MultipleResource response;

    public OnFinishWatchSuccess(MultipleResource response) {
        this.response = response;
    }
}
