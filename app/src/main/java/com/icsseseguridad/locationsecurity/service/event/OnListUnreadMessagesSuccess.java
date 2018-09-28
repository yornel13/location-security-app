package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListChatWithUnread;

public class OnListUnreadMessagesSuccess {

    public final ListChatWithUnread list;

    public OnListUnreadMessagesSuccess(ListChatWithUnread list) {
        this.list = list;
    }
}
