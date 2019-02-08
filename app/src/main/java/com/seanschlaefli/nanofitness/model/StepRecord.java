package com.seanschlaefli.nanofitness.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(entity = Workout.class,
                                  parentColumns = "id",
                                  childColumns = "workout_id"),
        indices = {@Index(value = {"workout_id"})}
)
public class StepRecord {

    public StepRecord(int workoutId, int recordStep, long recordTime) {
        mWorkoutId = workoutId;
        mRecordStep = recordStep;
        mRecordTime = recordTime;
    }

    @PrimaryKey(autoGenerate = true)
    public int mId;

    @ColumnInfo(name = "workout_id")
    public int mWorkoutId;

    @ColumnInfo(name = "record_step")
    public int mRecordStep;

    @ColumnInfo(name = "record_time")
    public long mRecordTime;

    @Ignore
    public int getStep() {
        return mRecordStep;
    }

    @Ignore
    public long getTime() {
        return mRecordTime;
    }
}
