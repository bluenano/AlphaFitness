package com.seanschlaefli.nanofitness.activity.model;

import com.seanschlaefli.nanofitness.database.entity.Workout;
import com.seanschlaefli.nanofitness.util.WorkoutMath;

import java.util.Calendar;
import java.util.List;

public class ProfileStatsFactory {

    public static final long msInOneWeek = 604800000;

    private ProfileStatsFactory() {}

    public static ProfileStats createAverageWeekly(List<Workout> workouts, int weight) {
        ProfileStats averageWeekly = createAllTime(workouts, weight);
        long minTime = getMinTime(workouts);
        long totalTime = Calendar.getInstance().getTimeInMillis() - minTime;
        if (isLongerThanAWeek(totalTime)) {
            setAverageWeeklyValues(averageWeekly, totalTime);
        }
        return averageWeekly;
    }

    public static ProfileStats createAllTime(List<Workout> workouts, int weight) {
        return allocateProfileStats(workouts, "All Time", weight);
    }

    public static ProfileStats allocateProfileStats(List<Workout> workouts, String title, int weight) {
        int count = 0;
        int totalSteps = 0;
        int totalTime = 0;
        float totalDistance = 0.0f;
        for (Workout workout: workouts) {
            count++;
            totalSteps += workout.getStepCount();
            totalTime += workout.getNumSeconds();
            totalDistance += workout.getDistance();
        }
        int totalCalories = WorkoutMath.calculateCaloriesBurned(
                totalSteps,
                weight
        );
        return new ProfileStats(title, totalDistance, totalTime, count, totalCalories);
    }

    private static long getMinTime(List<Workout> workouts) {
        long minTime = Long.MAX_VALUE;
        for (Workout workout: workouts) {
            long startTime = workout.getStartTime();
            if (startTime < minTime) {
                minTime = startTime;
            }
        }
        return minTime;
    }

    private static void setAverageWeeklyValues(ProfileStats stats, long totalTime) {
        float numWeeks = getNumWeeks(totalTime);
        stats.setDistance(stats.getDistance() / numWeeks);
        stats.setNumCalories((int) (stats.getNumCalories() / numWeeks));
        stats.setNumWorkouts((int) (stats.getNumWorkouts() / numWeeks));
        stats.setTime((int) (stats.getTime() / numWeeks));
    }

    public static boolean isLongerThanAWeek(long timeInMs) {
        return timeInMs > msInOneWeek;
    }

    public static float getNumWeeks(long timeInMS) {
        return (float) (timeInMS / msInOneWeek);
    }


}
