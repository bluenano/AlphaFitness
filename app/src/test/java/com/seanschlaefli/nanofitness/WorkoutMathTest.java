package com.seanschlaefli.nanofitness;

import com.seanschlaefli.nanofitness.util.WorkoutMath;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.seanschlaefli.nanofitness.util.WorkoutMath.*;

public class WorkoutMathTest {

    @Test
    public void calculateCaloriesBurned() {
        int weight = 160;
        int expectedCalories = 44;
        int caloriesBurned = WorkoutMath.calculateCaloriesBurned(STEPS, weight);
        assertEquals(expectedCalories, caloriesBurned);
    }

    @Test
    public void calculateAvgRateInMinPerMile() {
        int totalSteps = 1000;
        long totalTimeInMs = 120_000;
        float expectedAvgRateMale = 4.2f;
        float expectedAvgRateFemale = 4.8f;
        float maleResult = WorkoutMath.calculateAvgRateInMinPerMile(totalSteps, totalTimeInMs, true);
        float femaleResult = WorkoutMath.calculateAvgRateInMinPerMile(totalSteps, totalTimeInMs, false);
        assertEquals(expectedAvgRateMale, maleResult, 0.1);
        assertEquals(expectedAvgRateFemale, femaleResult, 0.1);
    }

    @Test
    public void calculateDistanceInMiles() {
        int totalSteps = 1000;
        float expectedDistanceMale = 0.47f;
        float expectedDistanceFemale = 0.41f;
        float maleResult = WorkoutMath.calculateDistanceInMiles(totalSteps, true);
        float femaleResult = WorkoutMath.calculateDistanceInMiles(totalSteps, false);
        assertEquals(expectedDistanceMale, maleResult, 0.1);
        assertEquals(expectedDistanceFemale, femaleResult, 0.1);
    }

    /*
    @Test
    public void isLongerThanAWeek() {
        assertFalse(WorkoutMath.isLongerThanAWeek(msInOneWeek));
        assertFalse(WorkoutMath.isLongerThanAWeek(msInOneWeek-1));
        assertTrue(WorkoutMath.isLongerThanAWeek(msInOneWeek+1));
    }
    */
}