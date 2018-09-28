package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Channel;

public class OnCreateChannelSuccess {

    public final Channel channel;

    public OnCreateChannelSuccess(Channel channel) {
        this.channel = channel;
    }
}
