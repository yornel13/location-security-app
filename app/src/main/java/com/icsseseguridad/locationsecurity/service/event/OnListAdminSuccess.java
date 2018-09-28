package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListAdmin;

public class OnListAdminSuccess {

    public final ListAdmin list;

    public OnListAdminSuccess(ListAdmin list) {
        this.list = list;
    }
}
