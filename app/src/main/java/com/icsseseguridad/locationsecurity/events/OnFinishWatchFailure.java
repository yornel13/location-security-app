package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnFinishWatchFailure {

    public final MultipleResource response;

    public OnFinishWatchFailure(MultipleResource response) {
        this.response = response;
    }
}
