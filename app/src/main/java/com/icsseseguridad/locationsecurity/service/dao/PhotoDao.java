package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.Photo;

import java.util.List;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photo")
    List<Photo> getAll();

    @Query("SELECT * FROM photo WHERE sync = 0")
    List<Photo> getAllUnsaved();

    @Query("SELECT * FROM photo where linked_id = :id and linked_type = :type limit 1")
    Photo getBy(long id, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Photo> photo);

    @Update
    void update(Photo photo);

    @Delete
    void delete(Photo photo);

    @Query("DELETE FROM photo")
    void deleteAll();
}
