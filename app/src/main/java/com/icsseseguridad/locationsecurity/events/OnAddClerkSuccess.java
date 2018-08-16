package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Clerk;

public class OnAddClerkSuccess {

    public final Clerk clerk;

    public OnAddClerkSuccess(Clerk clerk) {
        this.clerk = clerk;
    }
}
