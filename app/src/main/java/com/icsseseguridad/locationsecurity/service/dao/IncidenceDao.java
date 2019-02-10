package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.icsseseguridad.locationsecurity.service.entity.Incidence;

import java.util.List;

@Dao
public interface IncidenceDao {

    @Query("SELECT * FROM incidence")
    List<Incidence> getAllSync();

    @Query("SELECT * FROM incidence")
    LiveData<List<Incidence>> getAll();

    @Query("SELECT * FROM incidence WHERE id = :id LIMIT 1")
    Incidence findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Incidence> incidence);

    @Delete
    void delete(Incidence incidence);

    @Query("DELETE FROM incidence")
    void deleteAll();
}
