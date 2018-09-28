package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListChat;

public class OnListChatSuccess {

    public final ListChat list;

    public OnListChatSuccess(ListChat list) {
        this.list = list;
    }
}
