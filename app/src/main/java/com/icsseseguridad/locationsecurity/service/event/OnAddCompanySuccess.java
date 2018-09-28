package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.Company;

public class OnAddCompanySuccess {

    public final Company company;

    public OnAddCompanySuccess(Company company) {
        this.company = company;
    }
}
