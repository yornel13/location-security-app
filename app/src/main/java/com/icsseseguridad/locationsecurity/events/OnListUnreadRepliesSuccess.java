package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListRepliesWithUnread;

public class OnListUnreadRepliesSuccess {

    public final ListRepliesWithUnread list;

    public OnListUnreadRepliesSuccess(ListRepliesWithUnread list) {
        this.list = list;
    }
}
