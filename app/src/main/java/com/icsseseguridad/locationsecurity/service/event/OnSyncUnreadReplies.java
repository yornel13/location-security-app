package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListRepliesWithUnread;

public class OnSyncUnreadReplies {

    public final ListRepliesWithUnread list;

    public OnSyncUnreadReplies(ListRepliesWithUnread list) {
        this.list = list;
    }
}
