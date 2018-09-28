package com.icsseseguridad.locationsecurity.service.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MultipleResource {

    @SerializedName("result")
    public Object result;

    @SerializedName("response")
    public Boolean response;

    @SerializedName("message")
    public String message;

    @SerializedName("errors")
    public Object errors;

    public MultipleResource(Boolean response, String message) {
        this.response = response;
        this.message = message;
    }
}
