package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Clerk;

public class OnAddClerkSuccess {

    public final Clerk clerk;

    public OnAddClerkSuccess(Clerk clerk) {
        this.clerk = clerk;
    }
}
