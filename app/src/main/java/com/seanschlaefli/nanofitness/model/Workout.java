package com.seanschlaefli.nanofitness.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Workout {

    public Workout(long startTime) {
        this.mStartTime = startTime;
        mEndTime = 0;
        mStepCount = 0;
        mDistance = 0.0f;
        mNumSeconds = 0;
        mMaxRate = 0.0f;
        mMinRate = 0.0f;
        mAvgRate = 0.0f;
    }

    @Ignore
    public Workout(long startTime, long endTime,  int stepCount,
                   int numSeconds, float maxRate, float minRate,
                   float avgRate) {
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mStepCount = stepCount;
        this.mNumSeconds = numSeconds;
        this.mMaxRate = maxRate;
        this.mMinRate = minRate;
        this.mAvgRate = avgRate;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int mId;

    @ColumnInfo(name = "start_time")
    public long mStartTime;

    @ColumnInfo(name = "end_time")
    public long mEndTime;

    @ColumnInfo(name = "step_count")
    public int mStepCount;

    @ColumnInfo(name = "distance")
    public float mDistance;

    @ColumnInfo(name = "num_seconds")
    public int mNumSeconds;

    @ColumnInfo(name = "max_rate")
    public Float mMaxRate;

    @ColumnInfo(name = "min_rate")
    public Float mMinRate;

    @ColumnInfo(name = "avg_rate")
    public Float mAvgRate;

    @Ignore
    public int getId() {
        return mId;
    }

    @Ignore
    public long getStartTime() {
        return mStartTime;
    }

    @Ignore
    public long getEndTime() {
        return mEndTime;
    }

    @Ignore
    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    @Ignore
    public int getStepCount() {
        return mStepCount;
    }

    @Ignore
    public void setStepCount(int stepCount) {
        this.mStepCount = stepCount;
    }

    @Ignore
    public float getDistance() {
        return mDistance;
    }

    @Ignore
    public void setDistance(float distance) {
        mDistance = distance;
    }

    @Ignore
    public int getNumSeconds() {
        return mNumSeconds;
    }

    @Ignore
    public void incNumSeconds() {
        mNumSeconds++;
    }

    @Ignore
    public Float getMaxRate() {
        return mMaxRate;
    }

    @Ignore
    public void setMaxRate(Float maxRate) {
        this.mMaxRate = maxRate;
    }

    @Ignore
    public Float getMinRate() {
        return mMinRate;
    }

    @Ignore
    public void setMinRate(Float minRate) {
        this.mMinRate = minRate;
    }

    @Ignore
    public Float getAvgRate() {
        return mAvgRate;
    }

    @Ignore
    public void setAvgRate(Float avgRate) {
        this.mAvgRate = avgRate;
    }

    @Ignore
    public void updateRates(float newAvgRate, int numRecords) {
        if (!Float.isNaN(newAvgRate)) {
            mAvgRate = newAvgRate;
            updateMinRate(newAvgRate, numRecords);
            updateMaxRate(newAvgRate);
        }
    }

    @Ignore
    private void updateMinRate(float newAvgRate, int numRecords) {
        if (mMinRate == null) {
            if (numRecords > 5) {
                mMinRate = newAvgRate;
            }
            return;
        }
        if (newAvgRate < mMinRate) {
            mMinRate = newAvgRate;
        }
    }

    @Ignore
    private void updateMaxRate(float newAvgRate) {
        if (mMaxRate == null) {
            mMaxRate = newAvgRate;
            return;
        }
        if (newAvgRate > mMaxRate) {
            mMaxRate = newAvgRate;
        }
    }

}
