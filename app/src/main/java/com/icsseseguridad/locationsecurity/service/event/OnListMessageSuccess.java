package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListChatLine;

public class OnListMessageSuccess {

    public final ListChatLine list;

    public OnListMessageSuccess(ListChatLine list) {
        this.list = list;
    }
}
