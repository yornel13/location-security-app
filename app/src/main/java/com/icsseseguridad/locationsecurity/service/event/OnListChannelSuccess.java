package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListChannel;

public class OnListChannelSuccess {

    public final ListChannel list;

    public OnListChannelSuccess(ListChannel list) {
        this.list = list;
    }
}
