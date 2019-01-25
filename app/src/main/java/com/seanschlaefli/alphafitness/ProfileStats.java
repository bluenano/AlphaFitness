package com.seanschlaefli.alphafitness;

public class ProfileStats {

    private String mTitle;
    private float mDistance;
    private int mTime;
    private int mNumWorkouts;
    private int mNumCalories;

    public ProfileStats(String title) {
        mTitle = title;
        mDistance = 0.0f;
        mTime = 0;
        mNumWorkouts = 0;
        mNumCalories = 0;
    }

    public ProfileStats(String title, float distance, int time,
                        int numWorkouts, int numCalories) {
        mTitle = title;
        mDistance = distance;
        mTime = time;
        mNumWorkouts = numWorkouts;
        mNumCalories = numCalories;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    public int getNumWorkouts() {
        return mNumWorkouts;
    }

    public void setNumWorkouts(int numWorkouts) {
        mNumWorkouts = numWorkouts;
    }

    public int getNumCalories() {
        return mNumCalories;
    }

    public void setNumCalories(int numCalories) {
        mNumCalories = numCalories;
    }
}
