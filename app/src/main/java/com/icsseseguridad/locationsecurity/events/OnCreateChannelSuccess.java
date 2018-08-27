package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Channel;

public class OnCreateChannelSuccess {

    public final Channel channel;

    public OnCreateChannelSuccess(Channel channel) {
        this.channel = channel;
    }
}
