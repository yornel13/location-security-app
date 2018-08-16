package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ConfigUtility;

public class OnGetUpdateGpsSuccess {

    public final ConfigUtility utility;

    public OnGetUpdateGpsSuccess(ConfigUtility utility) {
        this.utility = utility;
    }
}
