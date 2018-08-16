package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListReply;

public class OnGetRepliesSuccess {

    public final ListReply list;

    public OnGetRepliesSuccess(ListReply list) {
        this.list = list;
    }
}
