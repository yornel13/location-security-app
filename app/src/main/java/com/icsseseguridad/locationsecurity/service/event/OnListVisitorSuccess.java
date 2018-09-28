package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListVisitor;

public class OnListVisitorSuccess {

    public final ListVisitor list;

    public OnListVisitorSuccess(ListVisitor list) {
        this.list = list;
    }
}
