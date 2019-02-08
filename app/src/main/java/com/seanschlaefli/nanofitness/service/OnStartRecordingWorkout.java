package com.seanschlaefli.nanofitness.service;

import com.seanschlaefli.nanofitness.database.entity.Workout;

public interface OnStartRecordingWorkout {
    void onStartRecordingWorkout(Workout workout);
}
