package com.icsseseguridad.locationsecurity.service.converter;

import android.arch.persistence.room.TypeConverter;

import com.icsseseguridad.locationsecurity.service.entity.Photo;

import java.sql.Timestamp;

/**
 * Created by yornel on 20/09/18
 */

public class LinkedTypeConverter {

    @TypeConverter
    public static Photo.LINKED toLINKED(String value) {
        return value == null ? null : Photo.LINKED.valueOf(value);
    }

    @TypeConverter
    public static String toString(Photo.LINKED value) {
        return value == null ? null : value.name();
    }
}
