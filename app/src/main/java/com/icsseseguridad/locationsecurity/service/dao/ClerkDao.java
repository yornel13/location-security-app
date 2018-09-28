package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.Clerk;

import java.util.List;

@Dao
public interface ClerkDao {

    @Query("SELECT * FROM clerk")
    LiveData<List<Clerk>> getAll();

    @Query("SELECT * FROM clerk WHERE dni LIKE :dni LIMIT 1")
    Clerk findByDni(String dni);

    @Query("SELECT * FROM clerk where id = :id LIMIT 1")
    Clerk findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Clerk> clerk);

    @Update
    void update(Clerk clerk);

    @Delete
    void delete(Clerk clerk);

    @Query("DELETE FROM clerk")
    void deleteAll();
}
