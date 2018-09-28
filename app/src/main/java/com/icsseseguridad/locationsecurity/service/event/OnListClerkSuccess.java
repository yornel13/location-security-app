package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListClerk;

public class OnListClerkSuccess {

    public final ListClerk list;

    public OnListClerkSuccess(ListClerk list) {
        this.list = list;
    }
}
