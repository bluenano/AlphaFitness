package com.seanschlaefli.nanofitness.activity.task;

import android.os.AsyncTask;
import android.util.Log;

import com.seanschlaefli.nanofitness.activity.OnWorkoutLoadComplete;
import com.seanschlaefli.nanofitness.model.Workout;
import com.seanschlaefli.nanofitness.viewmodel.WorkoutViewModel;

public class InsertWorkoutTask extends AsyncTask<Workout, Integer, Integer> {

    public static final String TAG = InsertWorkoutTask.class.getSimpleName();
    private WorkoutViewModel mWorkoutVM;
    private OnWorkoutLoadComplete mCallback;

    public InsertWorkoutTask(WorkoutViewModel workoutVM, OnWorkoutLoadComplete callback) {
        mWorkoutVM = workoutVM;
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(Workout... workouts) {
        if (workouts.length == 1) {
            return mWorkoutVM.insert(workouts[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.d(TAG, "onPostExecute of InsertWorkoutTask");
        mCallback.onWorkoutLoadComplete(integer);
    }

}
