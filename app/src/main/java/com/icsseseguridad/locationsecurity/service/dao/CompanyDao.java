package com.icsseseguridad.locationsecurity.service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.icsseseguridad.locationsecurity.service.entity.Company;

import java.util.List;

@Dao
public interface CompanyDao {

    @Query("SELECT * FROM company")
    LiveData<List<Company>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Company> company);

    @Update
    void update(Company company);

    @Delete
    void delete(Company company);

    @Query("DELETE FROM company")
    void deleteAll();
}
