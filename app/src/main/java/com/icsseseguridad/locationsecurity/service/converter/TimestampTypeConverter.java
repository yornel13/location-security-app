package com.icsseseguridad.locationsecurity.service.converter;

import android.arch.persistence.room.TypeConverter;

import java.sql.Timestamp;

/**
 * Created by yornel on 20/09/18
 */

public class TimestampTypeConverter {

    @TypeConverter
    public static Timestamp toTimestamp(Long value) {
        return value == null ? null : new Timestamp(value);
    }

    @TypeConverter
    public static Long toLong(Timestamp value) {
        return value == null ? null : value.getTime();
    }
}
