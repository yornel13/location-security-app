package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Watch;

public class OnInitWatchSuccess {

    public final Watch watch;

    public OnInitWatchSuccess(Watch watch) {
        this.watch = watch;
    }
}
