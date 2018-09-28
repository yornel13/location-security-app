package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

public class LastVisit {

    @SerializedName("visit_id")
    public Long visitId;

    @SerializedName("vehicle_id")
    public Long vehicleId;

    @SerializedName("visitor_id")
    public Long visitorId;

    @SerializedName("guard_id")
    public Long guardId;

    @SerializedName("visited_id")
    public Long clerkId;
}
