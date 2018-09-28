package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;

public class OnAddUsersToChannelSuccess {

    public final MultipleResource response;

    public OnAddUsersToChannelSuccess(MultipleResource response) {
        this.response = response;
    }
}
