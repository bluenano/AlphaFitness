package com.seanschlaefli.nanofitness;


import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

import java.util.List;

public class SaveWorkout extends Thread {

    public static final String TAG = SaveWorkout.class.getSimpleName();

    private Context mContext;
    private Workout mWorkout;

    public SaveWorkout(Context context, Workout workout) {
        mContext = context;
        mWorkout = workout;
    }

    @Override
    public void run() {
        ContentValues values = new ContentValues();
        values.put(FitnessContentProvider.UUID,
                mWorkout.getIdString());
        values.put(FitnessContentProvider.START_TIME,
                mWorkout.getStartTime());
        values.put(FitnessContentProvider.END_TIME,
                mWorkout.getEndTime());
        values.put(FitnessContentProvider.STEP_COUNT,
                mWorkout.getCurrentStepCount());
        values.put(FitnessContentProvider.TOTAL_TIME,
                mWorkout.getSeconds());
        mContext.getContentResolver().insert(FitnessContentProvider.URI_WORKOUT,
                values);
        saveLocations();
    }

    private void saveLocations() {
        List<Location> locations = mWorkout.getLocations();
        List<Long> locationRecordTimes = mWorkout.getLocationRecordTimes();
        String uuid = mWorkout.getIdString();
        int size = locations.size();
        if (size == locationRecordTimes.size()) {
            for (int i = 0; i < size; i++) {
                Location save = locations.get(i);
                long recordTime = locationRecordTimes.get(i);
                saveLocation(save, uuid, recordTime);
            }
        }
    }

    private void saveLocation(Location location, String workoutId, long recordTime) {
        ContentValues values = new ContentValues();
        values.put(FitnessContentProvider.WORKOUT_UUID,
                workoutId);
        values.put(FitnessContentProvider.LATITUDE,
                location.getLatitude());
        values.put(FitnessContentProvider.LONGITUDE,
                location.getLongitude());
        values.put(FitnessContentProvider.RECORD_TIME,
                recordTime);
        values.put(FitnessContentProvider.LOCATION_PROVIDER,
                location.getProvider());
        mContext.getContentResolver().insert(
                FitnessContentProvider.URI_LOCATION,
                values
        );
    }
}
