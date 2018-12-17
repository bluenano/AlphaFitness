package com.seanschlaefli.alphafitness;

public class WorkoutHistory {

    private int mTotalSteps;
    private int mNumWorkouts;
    private int mTotalTime;
    private long mStartTime;
    private long mEndTime;
    private boolean mIsMale;

    public WorkoutHistory() {
        mTotalSteps = 0;
        mNumWorkouts = 0;
        mTotalTime = 0;
        mStartTime = 0;
        mEndTime = 0;
        mIsMale = true;
    }

    public WorkoutHistory(int numWorkouts, int totalSteps, int totalTime,
                          boolean isMale, long startTime, long endTime) {
        mTotalSteps = totalSteps;
        mNumWorkouts = numWorkouts;
        mTotalTime = totalTime;
        mStartTime = startTime;
        mEndTime = endTime;
        mIsMale = isMale;
    }

    public int getNumWorkouts() {
        return mNumWorkouts;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    public void incNumWorkouts() {
        mNumWorkouts++;
    }

    public boolean isMale() {
        return mIsMale;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public float getNumWeeks() {
        long time = mEndTime - mStartTime;
        return (float) (time / 6.048e8);
    }

    public void addSteps(int steps) {
        mTotalSteps += steps;
    }

    public void addTime(int seconds) {
        mTotalTime += seconds;
    }

}
