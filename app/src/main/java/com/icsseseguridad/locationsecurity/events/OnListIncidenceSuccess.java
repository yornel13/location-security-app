package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListIncidence;

public class OnListIncidenceSuccess {

    public final ListIncidence list;

    public OnListIncidenceSuccess(ListIncidence list) {
        this.list = list;
    }
}
