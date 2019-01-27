package com.seanschlaefli.alphafitness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkoutMath {

    public static final String TAG = WorkoutMath.class.getSimpleName();

    static final float sMaleMultiplier = 2.5f;
    static final float sFemaleMultiplier = 2.2f;

    static final long msInOneWeek = 604800000;

    static final Integer[] WEIGHTS = new Integer[] {
            100, 120, 140, 160, 180, 200, 220, 250, 275, 300
    };

    // estimates the amount of calories burned by walking 1000 steps
    static final Map<Integer, Integer> WEIGHT_TO_CALORIES;
    static final int STEPS = 1000;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(WEIGHTS[0], 28);
        map.put(WEIGHTS[1], 33);
        map.put(WEIGHTS[2], 38);
        map.put(WEIGHTS[3], 44);
        map.put(WEIGHTS[4], 49);
        map.put(WEIGHTS[5], 55);
        map.put(WEIGHTS[6], 60);
        map.put(WEIGHTS[7], 69);
        map.put(WEIGHTS[8], 75);
        map.put(WEIGHTS[9], 82);
        WEIGHT_TO_CALORIES = Collections.unmodifiableMap(map);
    }

    public static int calculateCaloriesBurned(int stepCount, int weight) {
        int estimatedWeight = getWeightThreshold(weight);
        int caloriesPer1000Steps = WEIGHT_TO_CALORIES.get(estimatedWeight);
        float estimatedCals = ( (float) stepCount / STEPS ) * caloriesPer1000Steps;
        return (int) estimatedCals;
    }

    public static float calculateAvgRateInMinPerMile(int totalSteps, long totalTimeInMs, boolean isMale) {
        float multiplier = isMale ? sMaleMultiplier: sFemaleMultiplier;
        float minutes = UnitConverter.msToMinutes(totalTimeInMs);
        float miles = UnitConverter.stepCountToMiles(totalSteps, multiplier);
        return minutes / miles;
    }

    public static float calculateDistanceInMiles(int stepCount, boolean isMale) {
        float multiplier = isMale ? sMaleMultiplier : sFemaleMultiplier;
        float ft = UnitConverter.stepCountToFt(stepCount, multiplier);
        return UnitConverter.ftToMiles(ft);
    }

    public static int getWeightThreshold(int weight) {
        if (weight <= WEIGHTS[0]) {
            return WEIGHTS[0];
        }
        if (weight >= WEIGHTS[WEIGHTS.length-1]) {
            return WEIGHTS[WEIGHTS.length-1];
        }
        for (int i = 1; i < WEIGHTS.length-1; i++) {
            if (weight >= WEIGHTS[i] && weight < WEIGHTS[i+1]) {
                return WEIGHTS[i];
            }
        }
        return WEIGHTS[0];
    }

    public static boolean isLongerThanAWeek(long timeInMs) {
        return timeInMs > msInOneWeek;
    }

}
