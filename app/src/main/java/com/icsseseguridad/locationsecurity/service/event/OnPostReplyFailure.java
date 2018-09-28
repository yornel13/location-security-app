package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnPostReplyFailure {

    public final MultipleResource response;

    public OnPostReplyFailure(MultipleResource response) {
        this.response = response;
    }
}
