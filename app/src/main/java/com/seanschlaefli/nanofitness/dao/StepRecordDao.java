package com.seanschlaefli.nanofitness.dao;

import com.seanschlaefli.nanofitness.model.StepRecord;

import java.util.List;

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
    List<StepRecord> getAll();

    @Query("SELECT * FROM steprecord WHERE workout_id=:workoutId ORDER BY record_step ASC")
    List<StepRecord> loadByWorkoutId(final int workoutId);

    @Query("SELECT * FROM steprecord WHERE id=:stepRecordId")
    StepRecord loadById(int stepRecordId);
}
