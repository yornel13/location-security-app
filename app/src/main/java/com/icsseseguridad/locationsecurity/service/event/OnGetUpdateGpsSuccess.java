package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;

public class OnGetUpdateGpsSuccess {

    public final ConfigUtility utility;

    public OnGetUpdateGpsSuccess(ConfigUtility utility) {
        this.utility = utility;
    }
}
