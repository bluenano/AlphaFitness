package com.seanschlaefli.nanofitness.dao;

import com.seanschlaefli.nanofitness.model.LocationRecord;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface LocationRecordDao {

    @Insert
    long insert(LocationRecord locationRecord);

    @Update
    void update(LocationRecord... locationRecords);

    @Delete
    void delete(LocationRecord locationRecord);

    @Query("SELECT * FROM locationrecord")
    List<LocationRecord> getAll();

    @Query("SELECT * FROM locationrecord WHERE workout_id=:workoutId")
    List<LocationRecord> loadByWorkoutId(final int workoutId);

    @Query("SELECT * FROM locationrecord WHERE id=:locationRecordId")
    LocationRecord loadById(int locationRecordId);
}
