package com.icsseseguridad.locationsecurity.service.repository;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import java.util.UUID;

public class BaseController {

    protected static final String ICSSE_IMAGES = "icsse";

    protected Gson gson;

    protected AppPreferences preferences;

    public BaseController() {
        preferences = new AppPreferences(SecurityApp.getAppContext());
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    public String createToken() {
        return UUID.randomUUID().toString();
    }
}
