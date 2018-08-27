package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ChatLine;
import com.icsseseguridad.locationsecurity.model.Reply;

public class OnSendMessageSuccess {

    public final ChatLine message;

    public OnSendMessageSuccess(ChatLine message) {
        this.message = message;
    }
}
