package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnPostReplyFailure {

    public final MultipleResource response;

    public OnPostReplyFailure(MultipleResource response) {
        this.response = response;
    }
}
