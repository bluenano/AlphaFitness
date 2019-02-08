package com.seanschlaefli.nanofitness.viewmodel;

import android.app.Application;
import android.util.Log;

import com.seanschlaefli.nanofitness.database.entity.Workout;
import com.seanschlaefli.nanofitness.repo.LocalWorkoutRepository;
import com.seanschlaefli.nanofitness.repo.WorkoutRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WorkoutViewModel extends AndroidViewModel {

    public static final String TAG = WorkoutViewModel.class.getSimpleName();
    private WorkoutRepository mRepository;
    private LiveData<List<Workout>> mAllWorkouts;
    private LiveData<Workout> mCurrentWorkout;

    public WorkoutViewModel(@NonNull Application application) {//,WorkoutRepository repository) {
        super(application);
        //mRepository = repository;
        mRepository = new LocalWorkoutRepository(application);
        mAllWorkouts = mRepository.getAll();
    }

    public int insert(Workout workout) {
        Log.d(TAG, "In on insert");
        int id = mRepository.insert(workout);
        setCurrentWorkout(id);
        return id;
    }

    public LiveData<Workout> getWorkout(int workoutId) {
        return mRepository.getWorkout(workoutId);
    }

    public void update(Workout workout) {
        mRepository.update(workout);
    }

    public LiveData<List<Workout>> getAllWorkouts() {
        return mAllWorkouts;
    }

    public void setCurrentWorkout(int id) {
        mCurrentWorkout = mRepository.getWorkout(id);
        if (mCurrentWorkout == null) {
            Log.d(TAG, "Current workout is null in setCurrentWorkout");
        } else {
            Log.d(TAG, "Current workout is not null in setCurrentWorkout");
        }
    }

    public LiveData<Workout> getCurrentWorkout() {
        if (mCurrentWorkout == null) {
            Log.d(TAG, "getCurrent workout is null");
        }
        return mCurrentWorkout;
    }

    public void clearCurrentWorkout() {
        Log.d(TAG, "clearing current workout");
        mCurrentWorkout = null;
    }
}
