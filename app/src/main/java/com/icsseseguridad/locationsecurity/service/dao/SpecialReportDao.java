package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;

import java.util.List;

@Dao
public interface SpecialReportDao {

    @Query("SELECT * FROM special_report")
    LiveData<List<SpecialReport>> getAll();

    @Query("SELECT * FROM special_report WHERE sync = 0")
    List<SpecialReport> getAllUnsaved();

    @Query("SELECT * FROM special_report WHERE guard_id = :guardId ORDER BY update_date DESC")
    LiveData<List<SpecialReport>> findAllByGuardId(long guardId);

    @Query("SELECT * FROM special_report WHERE id = :id LIMIT 1")
    SpecialReport findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SpecialReport report);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SpecialReport> reports);

    @Update
    void update(SpecialReport report);

    @Delete
    void delete(SpecialReport report);

    @Query("DELETE FROM special_report")
    void deleteAll();
}
