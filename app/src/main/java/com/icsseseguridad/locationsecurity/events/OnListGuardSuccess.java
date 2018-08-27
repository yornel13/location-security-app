package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListCompany;
import com.icsseseguridad.locationsecurity.model.ListGuard;

public class OnListGuardSuccess {

    public final ListGuard list;

    public OnListGuardSuccess(ListGuard list) {
        this.list = list;
    }
}
