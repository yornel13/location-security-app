package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListCompany;

public class OnListCompanySuccess {

    public final ListCompany list;

    public OnListCompanySuccess(ListCompany list) {
        this.list = list;
    }
}
