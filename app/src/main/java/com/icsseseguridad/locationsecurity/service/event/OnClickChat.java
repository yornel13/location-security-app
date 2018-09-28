package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Chat;

public class OnClickChat {

    public final Chat chat;

    public OnClickChat(Chat chat) {
        this.chat = chat;
    }
}
