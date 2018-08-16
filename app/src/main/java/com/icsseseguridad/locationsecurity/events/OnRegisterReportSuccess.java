package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.SpecialReport;

public class OnRegisterReportSuccess {

    public final SpecialReport report;

    public OnRegisterReportSuccess(SpecialReport report) {
        this.report = report;
    }
}
