package com.icsseseguridad.locationsecurity.model;

import com.google.gson.annotations.SerializedName;

public class ReportWithUnread {

    @SerializedName("report")
    public SpecialReport report;

    @SerializedName("unread")
    public Integer unread;
}
