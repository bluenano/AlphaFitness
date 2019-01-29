package com.seanschlaefli.nanofitness.test;

import android.location.Location;

import com.seanschlaefli.nanofitness.Workout;

import java.util.ArrayList;
import java.util.List;

public class MockModels {
    private MockModels() {}

    public static String expectedUUID = "mock";
    public static long expectedStart = 1;
    public static long expectedEnd = 1000;
    public static int expectedSeconds = 10000;
    public static int expectedStepCount = 0;
    public static List<Integer> stepCounts;
    public static List<Long> stepCountRecordTimes;
    public static List<Location> locations;
    public static List<Long> locationRecordTimes;

    public static int expectedSizes = 2;
    public static int expectedStepCounts = 10;
    public static long expectedRecordTimes = 100;

    public static double expectedLat = 37.3;
    public static double expectedLong = -121.8;

    static {
        List<Integer> steps = new ArrayList<>();
        List<Long> stepsRecord = new ArrayList<>();
        List<Location> mockLocations = new ArrayList<>();
        List<Long> locationsRecord = new ArrayList<>();
        for (int i = 0; i < expectedSizes; i++) {
            steps.add(expectedStepCounts);
            stepsRecord.add(expectedRecordTimes);
            locationsRecord.add(expectedRecordTimes);
            Location current = new Location("");
            current.setLatitude(expectedLat);
            current.setLongitude(expectedLong);
            mockLocations.add(current);
        }
        stepCounts = steps;
        stepCountRecordTimes = stepsRecord;
        locations = mockLocations;
        locationRecordTimes = locationsRecord;
    }
    public static Workout getMockWorkout() {
        return new Workout(expectedUUID, expectedStart, expectedEnd,
                expectedSeconds, false, 0.0f,
                0.0f, 0.0f, stepCounts, stepCountRecordTimes,
                locations, locationRecordTimes);
    }
}
