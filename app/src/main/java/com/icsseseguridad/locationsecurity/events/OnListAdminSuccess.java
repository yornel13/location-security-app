package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListAdmin;

public class OnListAdminSuccess {

    public final ListAdmin list;

    public OnListAdminSuccess(ListAdmin list) {
        this.list = list;
    }
}
