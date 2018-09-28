package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Visitor;

public class OnAddVisitorSuccess {

    public final Visitor visitor;

    public OnAddVisitorSuccess(Visitor visitor) {
        this.visitor = visitor;
    }
}
