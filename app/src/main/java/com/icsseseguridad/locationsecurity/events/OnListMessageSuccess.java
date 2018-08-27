package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChatLine;
import com.icsseseguridad.locationsecurity.model.ListClerk;

public class OnListMessageSuccess {

    public final ListChatLine list;

    public OnListMessageSuccess(ListChatLine list) {
        this.list = list;
    }
}
