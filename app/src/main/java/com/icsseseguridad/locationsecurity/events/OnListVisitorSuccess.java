package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListVisitor;

public class OnListVisitorSuccess {

    public final ListVisitor list;

    public OnListVisitorSuccess(ListVisitor list) {
        this.list = list;
    }
}
