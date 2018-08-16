package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnFinishWatchSuccess {

    public final MultipleResource response;

    public OnFinishWatchSuccess(MultipleResource response) {
        this.response = response;
    }
}
