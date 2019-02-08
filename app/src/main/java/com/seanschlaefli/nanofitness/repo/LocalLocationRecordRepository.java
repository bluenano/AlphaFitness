package com.seanschlaefli.nanofitness.repo;

import android.app.Application;

import com.seanschlaefli.nanofitness.dao.LocationRecordDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.model.LocationRecord;

import java.util.List;

import androidx.lifecycle.LiveData;

public class LocalLocationRecordRepository implements LocationRecordRepository {

    public static final String TAG = LocalWorkoutRepository.class.getSimpleName();
    private LocationRecordDao mLocationRecordDao;
    private LiveData<List<LocationRecord>> mWorkoutLocations;

    public LocalLocationRecordRepository(Application application, int workoutId) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mLocationRecordDao = db.locationRecordDao();
        mWorkoutLocations = loadAllByWorkout(workoutId);
    }

    @Override
    public LiveData<List<LocationRecord>> getAll() {
        return mWorkoutLocations;
    }

    @Override
    public void insert(LocationRecord record) {
        mLocationRecordDao.insert(record);
    }

    @Override
    public void update(LocationRecord record) {
        mLocationRecordDao.update(record);
    }

    @Override
    public void delete(LocationRecord record) {
        mLocationRecordDao.delete(record);
    }

    @Override
    public LiveData<List<LocationRecord>> loadAllByWorkout(int workoutId) {
        return mLocationRecordDao.loadByWorkoutId(workoutId);
    }

}
