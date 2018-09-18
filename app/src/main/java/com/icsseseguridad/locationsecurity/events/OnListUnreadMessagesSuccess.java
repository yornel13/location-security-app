package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChatWithUnread;

public class OnListUnreadMessagesSuccess {

    public final ListChatWithUnread list;

    public OnListUnreadMessagesSuccess(ListChatWithUnread list) {
        this.list = list;
    }
}
