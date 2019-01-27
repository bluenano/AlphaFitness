package com.seanschlaefli.alphafitness;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.seanschlaefli.alphafitness.WorkoutMath.*;

public class WorkoutMathTest {

    private static int weight = 160;
    private static int expectedCalories = 44;

    @Test
    public void calculateCaloriesBurned() {
        int caloriesBurned = WorkoutMath.calculateCaloriesBurned(STEPS, weight);
        assertEquals(caloriesBurned, expectedCalories);
    }

    @Test
    public void calculateAvgRateInMinPerMile() {

    }

    @Test
    public void calculateDistanceInMiles() {

    }

    @Test
    public void isLongerThanAWeek() {
        assertFalse(WorkoutMath.isLongerThanAWeek(msInOneWeek));
        assertFalse(WorkoutMath.isLongerThanAWeek(msInOneWeek-1));
        assertTrue(WorkoutMath.isLongerThanAWeek(msInOneWeek+1));
    }
}