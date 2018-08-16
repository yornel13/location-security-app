package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Company;

public class OnAddCompanySuccess {

    public final Company company;

    public OnAddCompanySuccess(Company company) {
        this.company = company;
    }
}
