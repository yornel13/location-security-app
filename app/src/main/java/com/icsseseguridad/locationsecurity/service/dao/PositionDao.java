package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;

import java.util.List;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM position")
    List<TabletPosition> getAll();

    @Query("SELECT * FROM position WHERE sync = 0")
    List<TabletPosition> getAllUnsaved();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TabletPosition position);

    @Query("SELECT * FROM position ORDER BY id DESC LIMIT 1;")
    TabletPosition getLastInsert();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TabletPosition> positions);

    @Delete
    void delete(TabletPosition position);

    @Query("DELETE FROM banner")
    void deleteAll();
}
