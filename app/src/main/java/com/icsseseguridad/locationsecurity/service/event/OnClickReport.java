package com.icsseseguridad.locationsecurity.service.event;

import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;

public class OnClickReport {

    public final SpecialReport report;

    public OnClickReport(SpecialReport report) {
        this.report = report;
    }
}
