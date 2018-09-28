package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ChannelRegistered;

public class OnClickChannelAdd {

    public final ChannelRegistered channel;

    public OnClickChannelAdd(ChannelRegistered channel) {
        this.channel = channel;
    }
}
