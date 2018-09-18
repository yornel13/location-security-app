package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChatWithUnread;

public class OnSyncUnreadMessages {

    public final ListChatWithUnread list;

    public OnSyncUnreadMessages(ListChatWithUnread list) {
        this.list = list;
    }
}
