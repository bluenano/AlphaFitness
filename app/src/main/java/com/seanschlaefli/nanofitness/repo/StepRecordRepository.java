package com.seanschlaefli.nanofitness.repo;

import com.seanschlaefli.nanofitness.database.entity.StepRecord;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface StepRecordRepository {

    LiveData<List<StepRecord>> getAll();
    void insert(StepRecord record);
    void update(StepRecord record);
    void delete(StepRecord record);
    LiveData<List<StepRecord>> loadAllByWorkout(int workoutId);
}
