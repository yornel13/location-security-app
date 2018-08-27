package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.Clerk;

public class OnCreateChatSuccess {

    public final Chat chat;

    public OnCreateChatSuccess(Chat chat) {
        this.chat = chat;
    }
}
