package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListRepliesWithUnread;

public class OnListUnreadRepliesSuccess {

    public final ListRepliesWithUnread list;

    public OnListUnreadRepliesSuccess(ListRepliesWithUnread list) {
        this.list = list;
    }
}
