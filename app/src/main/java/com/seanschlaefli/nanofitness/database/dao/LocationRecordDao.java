package com.seanschlaefli.nanofitness.database.dao;

import com.seanschlaefli.nanofitness.database.entity.LocationRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
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
    LiveData<List<LocationRecord>> getAll();

    @Query("SELECT * FROM locationrecord WHERE workout_id=:workoutId")
    LiveData<List<LocationRecord>> loadByWorkoutId(final int workoutId);

    @Query("SELECT * FROM locationrecord WHERE mId=:locationRecordId")
    LiveData<LocationRecord> loadById(int locationRecordId);
}
