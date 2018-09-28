package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListGuard;

public class OnListGuardSuccess {

    public final ListGuard list;

    public OnListGuardSuccess(ListGuard list) {
        this.list = list;
    }
}
