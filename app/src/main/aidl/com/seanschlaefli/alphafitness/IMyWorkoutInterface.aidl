// IMyWorkoutInterface.aidl
package com.seanschlaefli.alphafitness;

// Decided not to expose a AIDL interface because of issues with
// orientation changes and binding

// A Remote Service without binding is the solution I went with

interface IMyWorkoutInterface {

    void startWorkout(long startTime, boolean isMale, int weight);
    void stopWorkout(long endTime);
    int getCurrentTimeInSeconds();
    int getCurrentStepCount();
    int[] getStepCounts();
    long[] getRecordTimes();
    int[] getCaloriesBurned(int weight);
    float getCurrentDistance();

}
