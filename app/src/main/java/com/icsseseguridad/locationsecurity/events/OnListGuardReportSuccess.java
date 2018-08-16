package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.ListReport;

public class OnListGuardReportSuccess {

    public final ListReport list;

    public OnListGuardReportSuccess(ListReport list) {
        this.list = list;
    }
}
