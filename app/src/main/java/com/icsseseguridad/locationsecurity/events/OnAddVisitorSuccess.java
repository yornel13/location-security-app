package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Visitor;

public class OnAddVisitorSuccess {

    public final Visitor visitor;

    public OnAddVisitorSuccess(Visitor visitor) {
        this.visitor = visitor;
    }
}
