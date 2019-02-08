package com.seanschlaefli.nanofitness.repo;

import com.seanschlaefli.nanofitness.model.Workout;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface WorkoutRepository {

    LiveData<List<Workout>> getAll();
    int insert(Workout workout);
    LiveData<Workout> getWorkout(int workoutId);
    void update(Workout workout);
    //void asyncInsertFinished(int workoutId);
    //LiveData<Workout> getCurrentWorkout();
}
