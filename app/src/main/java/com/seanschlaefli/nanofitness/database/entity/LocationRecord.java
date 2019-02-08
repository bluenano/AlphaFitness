package com.seanschlaefli.nanofitness.database.entity;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
        mWorkoutId = workoutId;
        mLatitude = latitude;
        mLongitude = longitude;
        mRecordTime = recordTime;
        mLocationProvider = locationProvider;
    }

    @PrimaryKey(autoGenerate = true)
    public int mId;

    @ColumnInfo(name = "workout_id")
    public int mWorkoutId;

    @ColumnInfo(name = "mLatitude")
    public double mLatitude;

    @ColumnInfo(name = "mLongitude")
    public double mLongitude;

    @ColumnInfo(name = "record_time")
    public long mRecordTime;

    @ColumnInfo(name = "location_provider")
    public String mLocationProvider;

    @Ignore
    public Location getLocation() {
        Location location = new Location(mLocationProvider);
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        return location;
    }
}
