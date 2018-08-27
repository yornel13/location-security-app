package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Chat;

public class OnClickChat {

    public final Chat chat;

    public OnClickChat(Chat chat) {
        this.chat = chat;
    }
}
