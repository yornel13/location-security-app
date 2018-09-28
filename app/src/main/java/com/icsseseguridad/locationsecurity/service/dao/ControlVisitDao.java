package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;

import java.util.List;

@Dao
public interface ControlVisitDao {

    @Query("SELECT * FROM control_visit")
    LiveData<List<ControlVisit>> getAll();

    @Query("SELECT * FROM control_visit WHERE sync = 0")
    List<ControlVisit> getAllUnsaved();

    @Query("SELECT * FROM control_visit WHERE vehicle_id = :vehicleId")
    List<ControlVisit> findAllByVehicleId(long vehicleId);

    @Query("SELECT * FROM control_visit WHERE visitor_id = :visitorId")
    List<ControlVisit> findAllByVisitorId(long visitorId);

    @Query("SELECT * FROM control_visit WHERE id = :id LIMIT 1")
    ControlVisit findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ControlVisit controlVisit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ControlVisit> visits);

    @Update
    void update(ControlVisit visit);

    @Query("UPDATE control_visit SET vehicle_id = :newId WHERE vehicle_id = :oldId")
    void updateVehicleId(long oldId, long newId);

    @Query("UPDATE control_visit SET visitor_id = :newId WHERE visitor_id = :oldId")
    void updateVisitorId(long oldId, long newId);

    @Delete
    void delete(ControlVisit visit);

    @Query("DELETE FROM control_visit")
    void deleteAll();
}
