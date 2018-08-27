package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListBanner;
import com.icsseseguridad.locationsecurity.model.ListReply;

public class OnGetBannersSuccess {

    public final ListBanner list;

    public OnGetBannersSuccess(ListBanner list) {
        this.list = list;
    }
}
