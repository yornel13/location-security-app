package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListReply;

public class OnGetRepliesSuccess {

    public final ListReply list;

    public OnGetRepliesSuccess(ListReply list) {
        this.list = list;
    }
}
