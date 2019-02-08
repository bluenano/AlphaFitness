package com.seanschlaefli.nanofitness.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import com.seanschlaefli.nanofitness.R;
import com.seanschlaefli.nanofitness.activity.task.InsertWorkoutTask;
import com.seanschlaefli.nanofitness.fragment.RecordWorkoutFragment;
import com.seanschlaefli.nanofitness.fragment.WorkoutDetailsFragment;
import com.seanschlaefli.nanofitness.util.WorkoutMath;
import com.seanschlaefli.nanofitness.service.WorkoutService;
import com.seanschlaefli.nanofitness.database.entity.LocationRecord;
import com.seanschlaefli.nanofitness.database.entity.StepRecord;
import com.seanschlaefli.nanofitness.database.entity.Workout;

import com.seanschlaefli.nanofitness.viewmodel.LocationRecordViewModel;
import com.seanschlaefli.nanofitness.viewmodel.MyViewModelFactory;
import com.seanschlaefli.nanofitness.viewmodel.StepRecordViewModel;
import com.seanschlaefli.nanofitness.viewmodel.WorkoutViewModel;

import java.util.Calendar;
import java.util.List;


public class RecordActivity extends AppCompatActivity
        implements RecordWorkoutFragment.OnRecordChange, OnWorkoutLoadComplete,
                   OnStepRecordLoadComplete, OnLocationRecordLoadComplete {

    public static final String TAG = RecordActivity.class.getSimpleName();
    private static final String IS_WORKOUT_STARTED_KEY = "is_workout_started_key";

    private boolean mIsWorkoutStarted;
    private int mWeight;

    private RecordWorkoutFragment mPortrait;
    private WorkoutDetailsFragment mLandscape;

    private WorkoutViewModel mWorkoutViewModel;
    private StepRecordViewModel mStepRecordViewModel;
    private LocationRecordViewModel mLocationRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        styleActionbar();

        mWorkoutViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "ProfilePreferences",
                MODE_PRIVATE
        );

        String weightStr = preferences.getString(ProfileActivity.WEIGHT_KEY, "");
        mWeight = ProfileActivity.getWeight(weightStr);


        if (savedInstanceState != null) {
            mIsWorkoutStarted = savedInstanceState.getBoolean(IS_WORKOUT_STARTED_KEY);
        } else {
            mIsWorkoutStarted = false;
        }

        LiveData<Workout> liveWorkout = mWorkoutViewModel.getCurrentWorkout();
        if (liveWorkout != null) {
            Workout workout = liveWorkout.getValue();
            if (workout != null) {
                liveWorkout.observe(this, getWorkoutObserver());
                setupStepRecordViewModel(workout.getId());
                setupLocationRecordViewModel(workout.getId());
                mStepRecordViewModel.getAllSteps().observe(this, getStepRecordObserver());
                mLocationRecordViewModel.getAllLocations().observe(this, getLocationRecordObserver());
            }
        }
        loadFragment();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (mIsWorkoutStarted) {
                Workout workout = mWorkoutViewModel.getCurrentWorkout().getValue();
                if (workout != null) {
                    workout.setEndTime(Calendar.getInstance().getTimeInMillis());
                    // make this call async
                    //mWorkoutViewModel.update(workout);
                }
            }
            stopService(new Intent(RecordActivity.this, WorkoutService.class));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_WORKOUT_STARTED_KEY, mIsWorkoutStarted);
    }

    @Override
    public void workoutStarted(long startTime) {
        Log.d(TAG, "In workout started");
        if (mIsWorkoutStarted) {
            return;
        }
        updateDuration(0);
        updateDistance(0.0f);
        final Workout workout = new Workout(startTime);
        new InsertWorkoutTask(mWorkoutViewModel, this)
                .execute(workout);
    }

    @Override
    public void workoutEnded(long endTime) {
        stopService(new Intent(this, WorkoutService.class));
        mWorkoutViewModel.getCurrentWorkout().removeObservers(this);
        Workout workout = mWorkoutViewModel.getCurrentWorkout().getValue();
        mWorkoutViewModel.clearCurrentWorkout();
        mIsWorkoutStarted = false;
        if (workout != null) {
            workout.setEndTime(endTime);
            // make this call async
            //mWorkoutViewModel.update(workout);
        }
    }

    @Override
    public void startProfileActivity(boolean isRecording) {
        int workoutId = -1;
        if (mIsWorkoutStarted) {
            Workout workout = mWorkoutViewModel.getCurrentWorkout().getValue();
            if (workout != null) {
                workoutId = workout.getId();
            }
        }
        Intent intent = ProfileActivity.newIntent(this, workoutId);
        startActivity(intent);
    }

    private void loadFragment() {
        int currentOrientation = getResources().getConfiguration().orientation;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLandscape = WorkoutDetailsFragment.newInstance();
            transaction.replace(R.id.fragment_container, mLandscape);
            mPortrait = null;
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mPortrait = RecordWorkoutFragment.newInstance();
            Bundle args = new Bundle();
            args.putBoolean(RecordWorkoutFragment.RECORD_KEY, mIsWorkoutStarted);
            mPortrait.setArguments(args);
            transaction.replace(R.id.fragment_container, mPortrait);
            mLandscape = null;
        }
        transaction.commit();
    }

    private void updateUI(Workout workout) {
        Workout current = mWorkoutViewModel.getCurrentWorkout().getValue();
        if (current != null && workout.getId() == current.getId()) {
            updateDuration(workout.getNumSeconds());
            updateDistance(WorkoutMath.calculateDistanceInMiles(
                    workout.getStepCount(),
                    isMale()
            ));
            updateRates(workout.getAvgRate(),
                    workout.getMinRate(),
                    workout.getMaxRate());
        }

    }

    private void updateGraphs(long startTime,
                              List<Float> stepsPerMin,
                              List<Integer> caloriesBurned,
                              List<Long> recordTimes) {
        if (mLandscape != null) {
            mLandscape.updateGraphs(startTime,
                    stepsPerMin,
                    caloriesBurned,
                    recordTimes);
        }
    }

    public void updateMap(List<Location> locations) {
        if (mPortrait != null) {
            mPortrait.updateMap(locations);
        }
    }

    private void updateDuration(int timeInSeconds) {
        if (mPortrait != null) {
            mPortrait.updateDuration(timeInSeconds);
        }
    }

    private void updateDistance(float distance) {
        if (mPortrait != null) {
            mPortrait.updateDistance(distance);
        }
    }

    private void updateRates(float avg, float min, float max) {
        if (mLandscape != null) {
            if (avg > 0.0f) {
                mLandscape.updateAvgRate(avg);
            }
            if (min > 0.0f) {
                mLandscape.updateMinRate(min);
            }
            if (max > 0.0f) {
                mLandscape.updateMaxRate(max);
            }
        }
    }

    private boolean isMale() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "ProfilePreferences",
                MODE_PRIVATE
        );
        String gender = preferences.getString(ProfileActivity.GENDER_KEY, "male");
        if (gender != null) {
            gender = gender.toLowerCase();
            return gender.equals("male");
        }
        return true;
    }

    private void styleActionbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.action_bar_record);
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setupStepRecordViewModel(int workoutId) {
        if (mStepRecordViewModel == null) {
            mStepRecordViewModel = ViewModelProviders.of(this,
                    new MyViewModelFactory(getApplication(), workoutId))
                    .get(StepRecordViewModel.class);
        }
        new LoadStepRecordsTask(this, mStepRecordViewModel, workoutId).execute();
    }

    private void setupLocationRecordViewModel(int workoutId) {
        if (mLocationRecordViewModel == null) {
            mLocationRecordViewModel = ViewModelProviders.of(this,
                    new MyViewModelFactory(getApplication(), workoutId))
                    .get(LocationRecordViewModel.class);
            new LoadLocationRecordsTask(this, mLocationRecordViewModel, workoutId).execute();
        }
    }

    private Observer<Workout> getWorkoutObserver() {
        return new Observer<Workout> () {
            @Override
            public void onChanged(Workout workout) {
                updateUI(workout);
            }
        };
    }

    private Observer<List<StepRecord>> getStepRecordObserver() {
        Workout workout = mWorkoutViewModel.getCurrentWorkout().getValue();
        final long startTime = workout != null ? workout.getStartTime()
                : Calendar.getInstance().getTimeInMillis();
        return new Observer<List<StepRecord>> () {
            @Override
            public void onChanged(List<StepRecord> stepRecords) {
                updateGraphs(startTime,
                        mStepRecordViewModel.getStepsPerMinute(),
                        mStepRecordViewModel.getCaloriesBurned(mWeight),
                        mStepRecordViewModel.getRecordTimes());
            }
        };
    }

    private Observer<List<LocationRecord>> getLocationRecordObserver() {
        return new Observer<List<LocationRecord>> () {
            @Override
            public void onChanged(List<LocationRecord> locationRecords) {
                updateMap(mLocationRecordViewModel.getLocationsFromRecords());
            }
        };
    }

    @Override
    public void onWorkoutLoadComplete(Integer id) {
        mIsWorkoutStarted = true;
        Intent intent = new Intent(RecordActivity.this, WorkoutService.class);
        intent.putExtra(WorkoutService.EXTRA_IS_MALE, isMale());
        intent.putExtra(WorkoutService.EXTRA_WORKOUT_ID, id);
        setupStepRecordViewModel(id);
        setupLocationRecordViewModel(id);
        startService(intent);
        mWorkoutViewModel.getCurrentWorkout().observe(this, getWorkoutObserver());
    }

    @Override
    public void onStepRecordLoadComplete() {
        mStepRecordViewModel.getCurrentSteps().observe(this, getStepRecordObserver());
    }

    @Override
    public void onLocationRecordLoadComplete() {
        mLocationRecordViewModel.getAllLocations().observe(this, getLocationRecordObserver());
    }

    private static class LoadStepRecordsTask extends AsyncTask<Void, Void, Void> {

        private OnStepRecordLoadComplete mCallback;
        private StepRecordViewModel mVM;
        private int mWorkoutId;

        public LoadStepRecordsTask(OnStepRecordLoadComplete callback,
                                   StepRecordViewModel vm, int workoutId) {
            mCallback = callback;
            mVM = vm;
            mWorkoutId = workoutId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mVM.setCurrentSteps(mWorkoutId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onStepRecordLoadComplete();
        }
    }

    private static class LoadLocationRecordsTask extends AsyncTask<Void, Void, Void> {

        private OnLocationRecordLoadComplete mCallback;
        private LocationRecordViewModel mVM;
        private int mWorkoutId;

        public LoadLocationRecordsTask(OnLocationRecordLoadComplete callback,
                                       LocationRecordViewModel vm, int workoutId) {
            mCallback = callback;
            mVM = vm;
            mWorkoutId = workoutId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mVM.setAllLocations(mWorkoutId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onLocationRecordLoadComplete();
        }
    }
}
