package com.seanschlaefli.nanofitness.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Workout.class,
                                  parentColumns = "id",
                                  childColumns = "workout_id"),
        indices = {@Index(value = {"workout_id"})}
)
public class LocationRecord {

    public LocationRecord(int workoutId, double latitude, double longitude,
                          long recordTime, String locationProvider) {
        this.workoutId = workoutId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordTime = recordTime;
        this.locationProvider = locationProvider;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "workout_id")
    public int workoutId;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "record_time")
    public long recordTime;

    @ColumnInfo(name = "location_provider")
    public String locationProvider;
}
