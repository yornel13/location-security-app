package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChatWithUnread;
import com.icsseseguridad.locationsecurity.model.ListRepliesWithUnread;

public class OnSyncUnreadReplies {

    public final ListRepliesWithUnread list;

    public OnSyncUnreadReplies(ListRepliesWithUnread list) {
        this.list = list;
    }
}
