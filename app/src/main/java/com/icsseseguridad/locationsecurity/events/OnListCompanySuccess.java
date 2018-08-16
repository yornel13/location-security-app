package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListCompany;

public class OnListCompanySuccess {

    public final ListCompany list;

    public OnListCompanySuccess(ListCompany list) {
        this.list = list;
    }
}
