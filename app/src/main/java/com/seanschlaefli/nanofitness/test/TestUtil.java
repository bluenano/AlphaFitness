package com.seanschlaefli.nanofitness.test;

import android.location.Location;

import com.seanschlaefli.nanofitness.model.LocationRecord;
import com.seanschlaefli.nanofitness.model.StepRecord;
import com.seanschlaefli.nanofitness.model.Workout;

public class TestUtil {
    private TestUtil() {}

    public static Workout createWorkout(long startTime, long endTime, int stepCount,
                                        int numSeconds, float maxRate, float minRate,
                                        float avgRate) {
        return new Workout(startTime, endTime, stepCount,
                numSeconds, maxRate, minRate, avgRate);
    }

    public static Workout createWorkout(long startTime) {
        return new Workout(startTime);
    }

    public static StepRecord createStepRecord(int workoutId, int steps, long recordTime) {
        return new StepRecord(workoutId, steps, recordTime);
    }

    public static LocationRecord createLocationRecord(int workoutId, double latitude,
                                                      double longitude, long recordTime,
                                                      String provider) {
        return new LocationRecord(workoutId, latitude, longitude, recordTime, provider);
    }
}
