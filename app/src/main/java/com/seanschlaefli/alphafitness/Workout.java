package com.seanschlaefli.alphafitness;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Workout {

    public static final String TAG = Workout.class.getSimpleName();

    private UUID mId;
    private long mStartTime;
    private long mEndTime;
    private int mSeconds;
    private boolean mIsMale;
    private Float mAvgRate;
    private Float mMinRate;
    private Float mMaxRate;
    private List<Integer> mStepCounts;
    private List<Long> mStepCountRecordTimes;
    private List<Location> mLocations;
    private List<Long> mLocationRecordTimes;

    public Workout(boolean isMale) {
        mId = UUID.randomUUID();
        mStepCounts = new ArrayList<>();
        mStepCountRecordTimes = new ArrayList<>();
        mLocations = new ArrayList<>();
        mLocationRecordTimes = new ArrayList<>();
        mStartTime = 0;
        mEndTime = 0;
        mSeconds = 0;
        mIsMale = isMale;
        mAvgRate = null;
        mMinRate = null;
        mMaxRate = null;
    }

    public Workout(Workout workout) {
        mId = workout.mId;
        mStepCounts = new ArrayList<>(workout.mStepCounts);
        mStepCountRecordTimes = new ArrayList<>(workout.mStepCountRecordTimes);
        mLocations = new ArrayList<>(workout.mLocations);
        mLocationRecordTimes = new ArrayList<>(workout.mLocationRecordTimes);
        mStartTime = workout.mStartTime;
        mEndTime = workout.mEndTime;
        mSeconds = workout.mSeconds;
        mIsMale = workout.mIsMale;
        mAvgRate = workout.mAvgRate;
        mMaxRate = workout.mMaxRate;
        mMinRate = workout.mMinRate;
    }

    public Workout(String uuid, long startTime, long endTime,
                   int seconds, boolean isMale, float avgRate,
                   float maxRate, float minRate, List<Integer> stepCounts,
                   List<Long> stepCountRecordTimes,
                   List<Location> locations,
                   List<Long> locationRecordTimes) {
        mId = UUID.fromString(uuid);
        mStartTime = startTime;
        mEndTime = endTime;
        mSeconds = seconds;
        mIsMale = isMale;
        mAvgRate = avgRate;
        mMaxRate = maxRate;
        mMinRate = minRate;
        mStepCounts = stepCounts;
        mStepCountRecordTimes = stepCountRecordTimes;
        mLocations = locations;
        mLocationRecordTimes = locationRecordTimes;
    }

    public String getIdString() {
        return mId.toString();
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public List<Integer> getStepCounts() {
        return new ArrayList<>(mStepCounts);
    }

    public void setStepCounts(List<Integer> stepCounts) {
        mStepCounts = new ArrayList<>(stepCounts);
    }

    public List<Long> getStepCountRecordTimes() {
        return new ArrayList<>(mStepCountRecordTimes);
    }

    public void setStepCountRecordTimes(List<Long> stepCountRecordTimes) {
        mStepCountRecordTimes = new ArrayList<>(stepCountRecordTimes);
    }

    public List<Location> getLocations() {
        return new ArrayList<>(mLocations);
    }

    public void setLocations(List<Location> locations) {
        mLocations = new ArrayList<>(locations);
    }

    public List<Long> getLocationRecordTimes() {
        return new ArrayList<>(mLocationRecordTimes);
    }

    public void setLocationRecordTimes(List<Long> locationRecordTimes) {
        mLocationRecordTimes = new ArrayList<>(locationRecordTimes);
    }

    public void addStepCount(int stepCount, long currentTime) {
        mStepCounts.add(stepCount);
        addStepCountRecordTime(currentTime);
    }

    public void addLocation(Location location, long currentTime) {
        mLocations.add(location);
        addLocationRecordTime(currentTime);
    }

    private void addStepCountRecordTime(long time) {
        mStepCountRecordTimes.add(time);
    }

    private void addLocationRecordTime(long time) {
        mLocationRecordTimes.add(time);
    }

    public int getCurrentStepCount() {
        int size = mStepCounts.size();
        if (size > 1) {
            return mStepCounts.get(size - 1) - mStepCounts.get(0);
        }
        return 0;
    }

    public long getTotalTime() {
        int size = mStepCountRecordTimes.size();
        if (size > 0) {
            return mStepCountRecordTimes.get(size - 1) - mStepCountRecordTimes.get(0);
        }
        return 0;
    }

    public void setAvgRate(float newRate) {
        mAvgRate = newRate;
    }

    public void setMaxRate(float newMax) {
        mMaxRate = newMax;
    }

    public void setMinRate(float newRate) {
        mMinRate = newRate;
    }

    public Float getAvgRate() {
        return mAvgRate == null ? 0.0f : mAvgRate;
    }

    public float getMinRate() {
        return mMinRate == null ? 0.0f : mMinRate;
    }

    public float getMaxRate() {
        return mMaxRate == null ? 0.0f : mMaxRate;
    }

    public void updateRates(float newAvgRate) {
        if (!Float.isNaN(newAvgRate)) {
            mAvgRate = newAvgRate;
            updateMinRate(newAvgRate);
            updateMaxRate(newAvgRate);
        }
    }

    private void updateMinRate(float newAvgRate) {
        int numberOfRecords = getStepCountRecordTimes().size();
        if (mMinRate == null) {
            if (numberOfRecords > 5) {
                mMinRate = newAvgRate;
            }
            return;
        }
        if (newAvgRate < mMinRate) {
            mMinRate = newAvgRate;
        }
    }

    private void updateMaxRate(float newAvgRate) {
        if (mMaxRate == null) {
            mMaxRate = newAvgRate;
            return;
        }
        if (newAvgRate > mMaxRate) {
            mMaxRate = newAvgRate;
        }
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void incSeconds() {
        mSeconds++;
    }

    public boolean isMale() {
        return mIsMale;
    }

    public List<Integer> getCaloriesBurned(int weight) {
        List<Integer> caloriesBurned = new ArrayList<>();
        int size = mStepCounts.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                caloriesBurned.add(
                        WorkoutMath.calculateCaloriesBurned(
                                mStepCounts.get(i) - mStepCounts.get(0),
                                weight
                        )
                );
            }
        }
        return caloriesBurned;
    }

    public List<Long> getRatesRecordTimes() {
        List<Long> stepsRecordTimes = new ArrayList<>();
        int size = mStepCounts.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                stepsRecordTimes.add(
                        mStepCountRecordTimes.get(i)
                );
            }
        }
        return stepsRecordTimes;
    }

    public List<Float> getStepsPerMinute() {
        List<Float> stepsPerMinute =  new ArrayList<>();
        int size = mStepCounts.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                int steps = mStepCounts.get(i) -
                        mStepCounts.get(0);
                long time = mStepCountRecordTimes.get(i) -
                        mStepCountRecordTimes.get(0);
                float minutes = UnitConverter.msToMinutes(time);
                stepsPerMinute.add(steps / minutes);
            }
        }
        return stepsPerMinute;
    }

    public Location getLatestLocation() {
        int size = mLocations.size();
        if (size > 0) {
            return mLocations.get(size-1);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Step Count: " + Integer.toString(getCurrentStepCount());
    }
}

