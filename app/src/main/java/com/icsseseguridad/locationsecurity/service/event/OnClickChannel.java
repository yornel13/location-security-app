package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ChannelRegistered;

public class OnClickChannel {

    public final ChannelRegistered channel;

    public OnClickChannel(ChannelRegistered channel) {
        this.channel = channel;
    }
}
