package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ChannelRegistered;

public class OnClickChannelAdd {

    public final ChannelRegistered channel;

    public OnClickChannelAdd(ChannelRegistered channel) {
        this.channel = channel;
    }
}
