package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.icsseseguridad.locationsecurity.service.entity.Banner;

import java.util.List;

@Dao
public interface BannerDao {

    @Query("SELECT * FROM banner")
    List<Banner> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Banner> banner);

    @Delete
    void delete(Banner banner);

    @Query("DELETE FROM banner")
    void deleteAll();
}
