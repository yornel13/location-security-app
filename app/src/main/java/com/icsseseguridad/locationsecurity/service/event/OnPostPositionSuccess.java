package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;

public class OnPostPositionSuccess {

    public final TabletPosition tabletPosition;

    public OnPostPositionSuccess(TabletPosition tabletPosition) {
        this.tabletPosition = tabletPosition;
    }
}
