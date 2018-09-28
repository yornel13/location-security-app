package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Reply;

public class OnPostReplySuccess {

    public final Reply reply;

    public OnPostReplySuccess(Reply reply) {
        this.reply = reply;
    }
}
