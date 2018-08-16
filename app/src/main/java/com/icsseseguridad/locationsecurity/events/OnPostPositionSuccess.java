package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.TabletPosition;

public class OnPostPositionSuccess {

    public final TabletPosition tabletPosition;

    public OnPostPositionSuccess(TabletPosition tabletPosition) {
        this.tabletPosition = tabletPosition;
    }
}
