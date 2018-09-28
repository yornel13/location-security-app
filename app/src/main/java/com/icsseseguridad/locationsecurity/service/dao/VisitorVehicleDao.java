package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;

import java.util.List;

@Dao
public interface VisitorVehicleDao {

    @Query("SELECT * FROM vehicle")
    LiveData<List<VisitorVehicle>> getAll();

    @Query("SELECT * FROM vehicle where id = :id LIMIT 1")
    VisitorVehicle findById(long id);

    @Query("SELECT * FROM vehicle WHERE sync = 0")
    List<VisitorVehicle> getAllUnsaved();

    @Query("SELECT * FROM vehicle WHERE plate LIKE :plate LIMIT 1")
    VisitorVehicle findByPlate(String plate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(VisitorVehicle vehicle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VisitorVehicle> vehicles);

    @Update
    void update(VisitorVehicle vehicles);

    @Delete
    void delete(VisitorVehicle vehicles);

    @Query("DELETE FROM vehicle")
    void deleteAll();
}
