package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ChannelRegistered;

public class OnClickChannel {

    public final ChannelRegistered channel;

    public OnClickChannel(ChannelRegistered channel) {
        this.channel = channel;
    }
}
