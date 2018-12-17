package com.seanschlaefli.alphafitness;

import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;

public class WorkoutHistoryFactory {

    public static final String TAG = WorkoutHistoryFactory.class.getSimpleName();

    public static WorkoutHistory from(Cursor workoutData, boolean isMale) {
        int numWorkouts = 0;
        int totalStepCount = 0;
        int totalTime = 0;
        long start = 0;
        if (workoutData.moveToFirst()) {
            start = getEarliestStartTime(workoutData);
            do {
                numWorkouts++;
                totalStepCount += workoutData.getInt(workoutData.getColumnIndex(
                        FitnessContentProvider.STEP_COUNT));
                totalTime += workoutData.getInt(
                        workoutData.getColumnIndex(
                                FitnessContentProvider.TOTAL_TIME));

            } while (workoutData.moveToNext());
        }
        Log.d(TAG, "Total number of workouts " + Integer.toString(numWorkouts));
        return new WorkoutHistory(numWorkouts, totalStepCount, totalTime,
                isMale, start, Calendar.getInstance().getTimeInMillis());
    }

    private static long getEarliestStartTime(Cursor workoutData) {
        return workoutData.getLong(workoutData.getColumnIndex(
                FitnessContentProvider.START_TIME
        ));
    }


}
