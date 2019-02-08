package com.seanschlaefli.nanofitness.database.dao;

import com.seanschlaefli.nanofitness.database.entity.StepRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StepRecordDao {

    @Insert
    long insert(StepRecord stepRecord);

    @Update
    void update(StepRecord... stepRecords);

    @Delete
    void delete(StepRecord stepRecord);

    @Query("SELECT * FROM steprecord")
    LiveData<List<StepRecord>> getAll();

    @Query("SELECT * FROM steprecord WHERE workout_id=:workoutId ORDER BY record_step ASC")
    LiveData<List<StepRecord>> loadByWorkoutId(final int workoutId);

    @Query("SELECT * FROM steprecord WHERE mId=:stepRecordId")
    LiveData<StepRecord> loadById(int stepRecordId);
}
