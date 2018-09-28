package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListChatWithUnread;

public class OnSyncUnreadMessages {

    public final ListChatWithUnread list;

    public OnSyncUnreadMessages(ListChatWithUnread list) {
        this.list = list;
    }
}
