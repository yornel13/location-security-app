package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.Visitor;

import java.util.List;

@Dao
public interface VisitorDao {

    @Query("SELECT * FROM visitor")
    LiveData<List<Visitor>> getAll();

    @Query("SELECT * FROM visitor WHERE sync = 0")
    List<Visitor> getAllUnsaved();

    @Query("SELECT * FROM visitor WHERE dni LIKE :dni LIMIT 1")
    Visitor findByDni(String dni);

    @Query("SELECT * FROM visitor WHERE id = :id LIMIT 1")
    Visitor findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Visitor visitor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Visitor> visitors);

    @Update
    void update(Visitor visitor);

    @Delete
    void delete(Visitor visitor);

    @Query("DELETE FROM visitor")
    void deleteAll();
}
