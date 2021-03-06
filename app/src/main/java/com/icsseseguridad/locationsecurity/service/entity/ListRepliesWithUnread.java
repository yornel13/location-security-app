package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListRepliesWithUnread {

    @SerializedName("data")
    public ArrayList<ReportWithUnread> reportsUnread;

    @SerializedName("unread")
    public Integer unread;
}
