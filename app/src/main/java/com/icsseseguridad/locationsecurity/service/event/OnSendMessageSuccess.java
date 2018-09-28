package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ChatLine;

public class OnSendMessageSuccess {

    public final ChatLine message;

    public OnSendMessageSuccess(ChatLine message) {
        this.message = message;
    }
}
