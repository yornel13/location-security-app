package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.icsseseguridad.locationsecurity.service.entity.Banner;
import com.icsseseguridad.locationsecurity.service.entity.Clerk;
import com.icsseseguridad.locationsecurity.service.entity.Company;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.service.converter.LinkedTypeConverter;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.converter.TimestampTypeConverter;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.VehicleType;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;

@Database(version = 39, entities = {
        Visitor.class,
        Clerk.class,
        VisitorVehicle.class,
        Company.class,
        ControlVisit.class,
        Banner.class,
        Photo.class,
        Incidence.class,
        SpecialReport.class,
        TabletPosition.class,
        VehicleType.class,
})
@TypeConverters({TimestampTypeConverter.class, LinkedTypeConverter.class})
public abstract  class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "location_security_db_v139";

    private static AppDatabase mInstance;

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    AppDatabase.DB_NAME
            ).build();
        }
        return mInstance;
    }

    public abstract VisitorDao getVisitorDao();
    public abstract ClerkDao getClerkDao();
    public abstract VisitorVehicleDao getVisitorVehicleDao();
    public abstract CompanyDao getCompanyDao();
    public abstract ControlVisitDao getControlVisitDao();
    public abstract BannerDao getBannerDao();
    public abstract PhotoDao getPhotoDao();
    public abstract IncidenceDao getIncidenceDao();
    public abstract SpecialReportDao getSpecialReportDao();
    public abstract PositionDao getPositionDao();
    public abstract VehicleTypeDao getVehicleTypeDao();
}
