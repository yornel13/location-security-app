package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Tablet;

public class OnVerifyTabletSuccess {

    public final Tablet tablet;

    public OnVerifyTabletSuccess(Tablet tablet) {
        this.tablet = tablet;
    }
}
