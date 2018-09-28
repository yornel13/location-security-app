package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.ListReport;

public class OnListGuardReportSuccess {

    public final ListReport list;

    public OnListGuardReportSuccess(ListReport list) {
        this.list = list;
    }
}
