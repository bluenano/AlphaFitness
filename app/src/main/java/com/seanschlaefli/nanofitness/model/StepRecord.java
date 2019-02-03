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
        this.workoutId = workoutId;
        this.recordStep = recordStep;
        this.recordTime = recordTime;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "workout_id")
    public int workoutId;

    @ColumnInfo(name = "record_step")
    public int recordStep;

    @ColumnInfo(name = "record_time")
    public long recordTime;
}
