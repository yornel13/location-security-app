package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Reply;

public class OnPostReplySuccess {

    public final Reply reply;

    public OnPostReplySuccess(Reply reply) {
        this.reply = reply;
    }
}
