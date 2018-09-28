package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;

public class OnRegisterReportSuccess {

    public final SpecialReport report;

    public OnRegisterReportSuccess(SpecialReport report) {
        this.report = report;
    }
}
