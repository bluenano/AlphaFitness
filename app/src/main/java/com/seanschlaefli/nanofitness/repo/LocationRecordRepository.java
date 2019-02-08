package com.seanschlaefli.nanofitness.repo;

import com.seanschlaefli.nanofitness.database.entity.LocationRecord;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface LocationRecordRepository {

    LiveData<List<LocationRecord>> getAll();
    void insert(LocationRecord record);
    void update(LocationRecord record);
    void delete(LocationRecord record);
    LiveData<List<LocationRecord>> loadAllByWorkout(int workoutId);

}
