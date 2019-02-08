package com.seanschlaefli.nanofitness.viewmodel;

import android.app.Application;

import com.seanschlaefli.nanofitness.util.UnitConverter;
import com.seanschlaefli.nanofitness.util.WorkoutMath;
import com.seanschlaefli.nanofitness.database.entity.StepRecord;
import com.seanschlaefli.nanofitness.repo.LocalStepRecordRepository;
import com.seanschlaefli.nanofitness.repo.StepRecordRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class StepRecordViewModel extends AndroidViewModel {

    public StepRecordRepository mRepository;
    public LiveData<List<StepRecord>> mAllSteps;
    public LiveData<List<StepRecord>> mCurrentSteps;

    public StepRecordViewModel(@NonNull Application application, Integer workoutId) {
        super(application);
        mRepository = new LocalStepRecordRepository(application, workoutId);
        mAllSteps = mRepository.getAll();
        mCurrentSteps = mRepository.getAll();
    }

    public LiveData<List<StepRecord>> getAllSteps() {
        return mAllSteps;
    }

    public LiveData<List<StepRecord>> getCurrentSteps() {
        return mCurrentSteps;
    }

    public void setCurrentSteps(int workoutId) {
        mCurrentSteps = mRepository.loadAllByWorkout(workoutId);
    }

    public List<Float> getStepsPerMinute() {
        List<StepRecord> records = mCurrentSteps.getValue();
        List<Float> stepsPerMinute =  new ArrayList<>();
        if (records != null) {
            int size = records.size();
            if (size > 1) {
                for (int i = 1; i < size; i++) {
                    int steps = records.get(i).getStep() -
                            records.get(0).getStep();
                    long time = records.get(i).getTime() -
                            records.get(0).getTime();
                    float minutes = UnitConverter.msToMinutes(time);
                    stepsPerMinute.add(steps / minutes);
                }
            }
        }
        return stepsPerMinute;
    }

    public List<Integer> getCaloriesBurned(int weight) {
        List<StepRecord> records = mCurrentSteps.getValue();
        List<Integer> caloriesBurned = new ArrayList<>();
        if (records != null) {
            int size = records.size();
            if (size > 1) {
                for (int i = 1; i < size; i++) {
                    caloriesBurned.add(
                            WorkoutMath.calculateCaloriesBurned(
                                    records.get(i).getStep() - records.get(0).getStep(),
                                    weight
                            )
                    );
                }
            }
        }
        return caloriesBurned;
    }

    public List<Long> getRecordTimes() {
        List<StepRecord> records = mCurrentSteps.getValue();
        List<Long> recordTimes = new ArrayList<>();
        if (records != null) {
            int size = records.size();
            if (size > 1) {
                for (int i = 1; i < size; i++) {
                    recordTimes.add(
                            records.get(i).getTime()
                    );
                }
            }
        }
        return recordTimes;
    }

}
