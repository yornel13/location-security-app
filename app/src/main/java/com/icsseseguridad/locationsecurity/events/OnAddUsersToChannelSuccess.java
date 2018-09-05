package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.MultipleResource;

public class OnAddUsersToChannelSuccess {

    public final MultipleResource response;

    public OnAddUsersToChannelSuccess(MultipleResource response) {
        this.response = response;
    }
}
