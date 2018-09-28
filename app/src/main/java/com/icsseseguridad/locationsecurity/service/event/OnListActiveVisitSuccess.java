package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListVisit;

public class OnListActiveVisitSuccess {

    public final ListVisit list;

    public OnListActiveVisitSuccess(ListVisit list) {
        this.list = list;
    }
}
