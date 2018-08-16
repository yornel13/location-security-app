package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnGetRepliesFailure {

    public final String message;

    public OnGetRepliesFailure(String message) {
        this.message = message;
    }
}
