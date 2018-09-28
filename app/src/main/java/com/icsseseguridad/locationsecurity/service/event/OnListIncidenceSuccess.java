package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListIncidence;

public class OnListIncidenceSuccess {

    public final ListIncidence list;

    public OnListIncidenceSuccess(ListIncidence list) {
        this.list = list;
    }
}
