package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Chat;

public class OnCreateChatSuccess {

    public final Chat chat;

    public OnCreateChatSuccess(Chat chat) {
        this.chat = chat;
    }
}
