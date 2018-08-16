package com.icsseseguridad.locationsecurity.events;

import com.icsseseguridad.locationsecurity.model.SpecialReport;

public class OnClickReport {

    public final SpecialReport report;

    public OnClickReport(SpecialReport report) {
        this.report = report;
    }
}
