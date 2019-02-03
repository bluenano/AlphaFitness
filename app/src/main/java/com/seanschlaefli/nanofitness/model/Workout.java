package com.seanschlaefli.nanofitness.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Workout {

    public Workout(long startTime) {
        this.startTime = startTime;
        endTime = 0;
        stepCount = 0;
        numSeconds = 0;
        maxRate = 0.0f;
        minRate = 0.0f;
        avgRate = 0.0f;
    }

    @Ignore
    public Workout(long startTime, long endTime,  int stepCount,
                   int numSeconds, float maxRate, float minRate,
                   float avgRate) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.stepCount = stepCount;
        this.numSeconds = numSeconds;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.avgRate = avgRate;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "start_time")
    public long startTime;

    @ColumnInfo(name = "end_time")
    public long endTime;

    @ColumnInfo(name = "step_count")
    public int stepCount;

    @ColumnInfo(name = "num_seconds")
    public int numSeconds;

    @ColumnInfo(name = "max_rate")
    public Float maxRate;

    @ColumnInfo(name = "min_rate")
    public Float minRate;

    @ColumnInfo(name = "avg_rate")
    public Float avgRate;

    @Ignore
    public int getId() {
        return id;
    }

    @Ignore
    public long getStartTime() {
        return startTime;
    }

    @Ignore
    public long getEndTime() {
        return endTime;
    }

    @Ignore
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Ignore
    public int getStepCount() {
        return stepCount;
    }

    @Ignore
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @Ignore
    public int getNumSeconds() {
        return numSeconds;
    }

    @Ignore
    public void incNumSeconds() {
        numSeconds++;
    }

    @Ignore
    public Float getMaxRate() {
        return maxRate;
    }

    @Ignore
    public void setMaxRate(Float maxRate) {
        this.maxRate = maxRate;
    }

    @Ignore
    public Float getMinRate() {
        return minRate;
    }

    @Ignore
    public void setMinRate(Float minRate) {
        this.minRate = minRate;
    }

    @Ignore
    public Float getAvgRate() {
        return avgRate;
    }

    @Ignore
    public void setAvgRate(Float avgRate) {
        this.avgRate = avgRate;
    }

    @Ignore
    public void updateRates(float newAvgRate, int numRecords) {
        if (!Float.isNaN(newAvgRate)) {
            avgRate = newAvgRate;
            updateMinRate(newAvgRate, numRecords);
            updateMaxRate(newAvgRate);
        }
    }

    @Ignore
    private void updateMinRate(float newAvgRate, int numRecords) {
        if (minRate == null) {
            if (numRecords > 5) {
                minRate = newAvgRate;
            }
            return;
        }
        if (newAvgRate < minRate) {
            minRate = newAvgRate;
        }
    }

    @Ignore
    private void updateMaxRate(float newAvgRate) {
        if (maxRate == null) {
            maxRate = newAvgRate;
            return;
        }
        if (newAvgRate > maxRate) {
            maxRate = newAvgRate;
        }
    }

}
