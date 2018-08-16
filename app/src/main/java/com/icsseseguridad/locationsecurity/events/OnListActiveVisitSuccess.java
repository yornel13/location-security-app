package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListVisit;

public class OnListActiveVisitSuccess {

    public final ListVisit list;

    public OnListActiveVisitSuccess(ListVisit list) {
        this.list = list;
    }
}
