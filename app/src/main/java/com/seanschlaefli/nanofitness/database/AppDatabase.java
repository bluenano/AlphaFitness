package com.seanschlaefli.nanofitness.database;

import com.seanschlaefli.nanofitness.dao.LocationRecordDao;
import com.seanschlaefli.nanofitness.dao.StepRecordDao;
import com.seanschlaefli.nanofitness.dao.WorkoutDao;
import com.seanschlaefli.nanofitness.model.LocationRecord;
import com.seanschlaefli.nanofitness.model.StepRecord;
import com.seanschlaefli.nanofitness.model.Workout;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Workout.class, LocationRecord.class, StepRecord.class},
                        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();
    public abstract StepRecordDao stepRecordDao();
    public abstract LocationRecordDao locationRecordDao();
}
