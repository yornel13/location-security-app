package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Watch;

public class OnInitWatchSuccess {

    public final Watch watch;

    public OnInitWatchSuccess(Watch watch) {
        this.watch = watch;
    }
}
