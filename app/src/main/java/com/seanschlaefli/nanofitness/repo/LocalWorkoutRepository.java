package com.seanschlaefli.nanofitness.repo;

import android.app.Application;
import android.os.AsyncTask;

import com.seanschlaefli.nanofitness.database.dao.WorkoutDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.database.entity.Workout;

import java.util.List;

import androidx.lifecycle.LiveData;

public class LocalWorkoutRepository implements WorkoutRepository {

    public static final String TAG = LocalWorkoutRepository.class.getSimpleName();
    private WorkoutDao mWorkoutDao;
    private LiveData<List<Workout>> mAllWorkouts;

    public LocalWorkoutRepository(Application application) {
        mWorkoutDao = AppDatabase.getDatabase(application).workoutDao();
        mAllWorkouts = mWorkoutDao.getAll();
    }

    @Override
    public LiveData<List<Workout>> getAll() {
        return mAllWorkouts;
    }

    @Override
    public int insert(Workout workout) {
        return (int) mWorkoutDao.insert(workout);
    }

    @Override
    public LiveData<Workout> getWorkout(int workoutId) {
        return mWorkoutDao.loadById(workoutId);
    }

    @Override
    public void update(Workout workout) {
        mWorkoutDao.update(workout);
    }

    /*
    @Override
    public void asyncInsertFinished(int workoutId) {
        mCurrentWorkoutId = workoutId;
    }

    @Override
    public LiveData<Workout> getCurrentWorkout() {
        return mDb.workoutDao().loadById(mCurrentWorkoutId);
    }
    */

    private static class InsertAsyncTask extends AsyncTask<Workout, Void, Integer> {

        private WorkoutRepository mDelegate;
        private WorkoutDao mWorkoutDao;

        public InsertAsyncTask(WorkoutRepository delegate, WorkoutDao workoutDao) {
            mDelegate = delegate;
            mWorkoutDao = workoutDao;
        }

        @Override
        protected Integer doInBackground(Workout... workouts) {
            if (workouts.length == 1) {
                return (int) mWorkoutDao.insert(workouts[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //mDelegate.asyncInsertFinished(integer);
        }
    }

}
