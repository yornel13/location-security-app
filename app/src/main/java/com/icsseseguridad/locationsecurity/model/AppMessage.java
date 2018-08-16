package com.icsseseguridad.locationsecurity.model;

import java.sql.Timestamp;

public class AppMessage {

    public Long id;
    public String userName;
    public String message;
    public Timestamp createDate;
    public FROM from;
    public String receptorId;

    public enum FROM {
        ANDROID, WEB
    }
}
