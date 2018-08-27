package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChat;
import com.icsseseguridad.locationsecurity.model.ListChatLine;

public class OnListChatSuccess {

    public final ListChat list;

    public OnListChatSuccess(ListChat list) {
        this.list = list;
    }
}
