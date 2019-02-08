package com.seanschlaefli.nanofitness.repo;

import android.app.Application;

import com.seanschlaefli.nanofitness.dao.StepRecordDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.model.StepRecord;

import java.util.List;

import androidx.lifecycle.LiveData;

public class LocalStepRecordRepository implements StepRecordRepository {

    private Application mApplication;
    private StepRecordDao mStepRecordDao;

    private LiveData<List<StepRecord>> mWorkoutStepRecords;

    public LocalStepRecordRepository(Application application, int workoutId) {
        mApplication = application;
        mStepRecordDao = AppDatabase.getDatabase(mApplication).stepRecordDao();
        mWorkoutStepRecords = mStepRecordDao.loadByWorkoutId(workoutId);
    }

    @Override
    public LiveData<List<StepRecord>> getAll() {
        return mWorkoutStepRecords;
    }

    @Override
    public void insert(StepRecord record) {
        mStepRecordDao.insert(record);
    }

    @Override
    public void update(StepRecord record) {
        mStepRecordDao.update(record);
    }

    @Override
    public void delete(StepRecord record) {
        mStepRecordDao.delete(record);
    }

    @Override
    public LiveData<List<StepRecord>> loadAllByWorkout(int workoutId) {
        return mStepRecordDao.loadByWorkoutId(workoutId);
    }


}
