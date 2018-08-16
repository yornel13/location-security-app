package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.Company;

public class OnAddCompanyFailure {

    public final String message;

    public OnAddCompanyFailure(String message) {
        this.message = message;
    }
}
