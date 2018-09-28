package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListBanner;

public class OnGetBannersSuccess {

    public final ListBanner list;

    public OnGetBannersSuccess(ListBanner list) {
        this.list = list;
    }
}
