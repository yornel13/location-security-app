package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListClerk;

public class OnListClerkSuccess {

    public final ListClerk list;

    public OnListClerkSuccess(ListClerk list) {
        this.list = list;
    }
}
