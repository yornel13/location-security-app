package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListChannel;

public class OnListChannelSuccess {

    public final ListChannel list;

    public OnListChannelSuccess(ListChannel list) {
        this.list = list;
    }
}
