package com.seanschlaefli.nanofitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends AppCompatActivity
        implements RecordWorkoutFragment.OnRecordChange {

    public static final String TAG = RecordActivity.class.getSimpleName();
    private static final String IS_WORKOUT_STARTED_KEY = "is_workout_started";
    private static final String UUID_KEY = "uuid_key";
    private static final String START_TIME_KEY = "start_time_key";
    private static final String END_TIME_KEY = "end_time_key";
    private static final String DURATION_KEY = "duration_key";
    private static final String AVG_RATE_KEY = "avg_rate_key";
    private static final String MAX_RATE_KEY = "max_rate_key";
    private static final String MIN_RATE_KEY = "min_rate_key";
    private static final String STEP_COUNTS_KEY = "step_counts_key";
    private static final String STEP_COUNTS_RECORD_TIMES_KEY = "step_counts_record_times_key";
    private static final String LOCATIONS_KEY = "locations_key";
    private static final String LOCATIONS_RECORD_TIMES_KEY = "locations_record_times_key";

    private HandlerThread mHandlerThread;
    private Handler mBroadcastHandler;
    private Handler mMainHandler;
    private BroadcastReceiver mReceiver;

    private boolean mIsWorkoutStarted;
    private int mWeight;

    private Workout mWorkout;

    private RecordWorkoutFragment mPortrait;
    private WorkoutDetailsFragment mLandscape;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        styleActionbar();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "ProfilePreferences",
                MODE_PRIVATE
        );

        String weightStr = preferences.getString(ProfileActivity.WEIGHT_KEY, "");
        mWeight = ProfileActivity.getWeight(weightStr);

        if (savedInstanceState != null) {
            mIsWorkoutStarted = savedInstanceState.getBoolean(IS_WORKOUT_STARTED_KEY);
            initializeWorkout(savedInstanceState);
        } else {
            mIsWorkoutStarted = false;
            mWorkout = new Workout(isMale());
        }

        initializeFragments();
        Configuration config = getResources().getConfiguration();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            transaction.replace(R.id.fragment_container, mLandscape);
            mPortrait = null;
        } else {
            transaction.replace(R.id.fragment_container, mPortrait);
            mLandscape = null;
        }
        transaction.commit();

        initializeHandlers();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleWorkoutBroadcast(intent);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("com.seanschlaefli.alphafitness.CUSTOM_INTENT");
        this.registerReceiver(mReceiver, intentFilter, null, mBroadcastHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
        if (isFinishing()) {
            stopService(new Intent(RecordActivity.this, WorkoutService.class));
            mHandlerThread.quit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setSaveBundle(outState, new Workout(mWorkout));
    }

    public void setSaveBundle(Bundle outState, Workout workout) {
        outState.putBoolean(IS_WORKOUT_STARTED_KEY, mIsWorkoutStarted);
        outState.putString(UUID_KEY, workout.getIdString());
        outState.putLong(START_TIME_KEY, workout.getStartTime());
        outState.putLong(END_TIME_KEY, workout.getEndTime());
        outState.putInt(DURATION_KEY, workout.getSeconds());
        outState.putFloat(AVG_RATE_KEY, workout.getAvgRate());
        outState.putFloat(MAX_RATE_KEY, workout.getMaxRate());
        outState.putFloat(MIN_RATE_KEY, workout.getMinRate());
        outState.putIntArray(STEP_COUNTS_KEY,
                NanoFitnessUtil.convertIntList(workout.getStepCounts()));
        outState.putLongArray(STEP_COUNTS_RECORD_TIMES_KEY,
                NanoFitnessUtil.convertLongList(workout.getStepCountRecordTimes()));
        outState.putParcelableArrayList(LOCATIONS_KEY,
                (ArrayList<Location>) workout.getLocations());
        outState.putLongArray(LOCATIONS_RECORD_TIMES_KEY,
                NanoFitnessUtil.convertLongList(workout.getLocationRecordTimes()));
    }

    private void initializeFragments() {
        mLandscape = WorkoutDetailsFragment.newInstance();
        mPortrait = RecordWorkoutFragment.newInstance();

        Bundle landscapeArgs = getLandscapeArgs(new Workout(mWorkout));
        Bundle portraitArgs = getPortraitArgs(new Workout(mWorkout));
        portraitArgs.putBoolean(RecordWorkoutFragment.RECORD_KEY, mIsWorkoutStarted);

        mLandscape.setArguments(landscapeArgs);
        mPortrait.setArguments(portraitArgs);
    }

    private void initializeHandlers() {
        mHandlerThread = new HandlerThread("Workout Broadcasts");
        mHandlerThread.start();
        mBroadcastHandler = new Handler(mHandlerThread.getLooper());
        mMainHandler = new Handler();
    }

    public void initializeWorkout(Bundle inState) {
        mWorkout = new Workout(
                inState.getString(UUID_KEY),
                inState.getLong(START_TIME_KEY),
                inState.getLong(END_TIME_KEY),
                inState.getInt(DURATION_KEY),
                isMale(),
                inState.getFloat(AVG_RATE_KEY),
                inState.getFloat(MAX_RATE_KEY),
                inState.getFloat(MIN_RATE_KEY),
                NanoFitnessUtil.convertIntArray(
                        inState.getIntArray(STEP_COUNTS_KEY)),
                NanoFitnessUtil.convertLongArray(
                        inState.getLongArray(STEP_COUNTS_RECORD_TIMES_KEY)),
                inState.<Location>getParcelableArrayList(
                        LOCATIONS_KEY),
                NanoFitnessUtil.convertLongArray(
                        inState.getLongArray(LOCATIONS_RECORD_TIMES_KEY)
                )
        );
    }

    private Bundle getPortraitArgs(Workout workout) {
        Bundle args = new Bundle();
        args.putBoolean(RecordWorkoutFragment.RECORD_KEY, mIsWorkoutStarted);
        args.putFloat(RecordWorkoutFragment.DISTANCE_KEY,
                WorkoutMath.calculateDistanceInMiles(
                workout.getCurrentStepCount(),
                workout.isMale()
        ));
        args.putInt(RecordWorkoutFragment.TIME_KEY,
                workout.getSeconds());
        args.putParcelableArrayList(RecordWorkoutFragment.LOCATIONS_KEY,
                (ArrayList<Location>) workout.getLocations());
        return args;
    }

    private Bundle getLandscapeArgs(Workout workout) {
        Bundle args = new Bundle();
        args.putLong(WorkoutDetailsFragment.START_TIME_KEY,
                workout.getStartTime());
        args.putFloat(WorkoutDetailsFragment.AVG_RATE_KEY,
                workout.getAvgRate());
        args.putFloat(WorkoutDetailsFragment.MAX_RATE_KEY,
                workout.getMaxRate());
        args.putFloat(WorkoutDetailsFragment.MIN_RATE_KEY,
                workout.getMinRate());
        args.putFloatArray(WorkoutDetailsFragment.STEPS_PER_MIN_KEY,
                NanoFitnessUtil.convertFloatList(workout.getStepsPerMinute()));
        args.putIntArray(WorkoutDetailsFragment.CALORIES_BURNED_KEY,
                NanoFitnessUtil.convertIntList(workout.getCaloriesBurned(mWeight)));
        args.putLongArray(WorkoutDetailsFragment.RATES_RECORD_TIMES_KEY,
                NanoFitnessUtil.convertLongList(workout.getRatesRecordTimes()));
        return args;
    }

    @Override
    public void workoutStarted(long startTime) {
        if (mIsWorkoutStarted) {
            return;
        }
        updateDuration(0);
        updateDistance(0.0f);
        mWorkout = new Workout(isMale());
        mWorkout.setStartTime(startTime);
        mIsWorkoutStarted = true;

        Intent intent = new Intent(RecordActivity.this, WorkoutService.class);
        intent.putExtra(WorkoutService.EXTRA_IS_MALE, mWorkout.isMale());
        intent.putExtra(WorkoutService.EXTRA_START_TIME, mWorkout.getStartTime());
        startService(intent);
    }

    @Override
    public void workoutEnded(long endTime) {
        mIsWorkoutStarted = false;
        stopService(new Intent(this, WorkoutService.class));
        mWorkout.setEndTime(endTime);
        saveWorkout(new Workout(mWorkout));
    }

    @Override
    public void startProfileActivity(boolean isRecording) {
        int stepCount = 0;
        int seconds = 0;
        if (mIsWorkoutStarted) {
            stepCount = mWorkout.getCurrentStepCount();
            seconds = mWorkout.getSeconds();
        }
        Intent intent = ProfileActivity.newIntent(this, isRecording,
                stepCount, seconds);
        startActivity(intent);
    }


    // this runs on a background thread using HandlerThread
    private void handleWorkoutBroadcast(Intent intent) {
        if (mIsWorkoutStarted) {
            if (intent.hasExtra(WorkoutService.EXTRA_TOTAL_TIME)) {
                int seconds = intent.getIntExtra(WorkoutService.EXTRA_TOTAL_TIME, 0);
                mWorkout.setSeconds(seconds);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_AVG_RATE)) {
                float avgRate = intent.getFloatExtra(WorkoutService.EXTRA_AVG_RATE, 0.0f);
                mWorkout.setAvgRate(avgRate);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_MAX_RATE)) {
                float maxRate = intent.getFloatExtra(WorkoutService.EXTRA_MAX_RATE, 0.0f);
                mWorkout.setMaxRate(maxRate);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_MIN_RATE)) {
                float minRate = intent.getFloatExtra(WorkoutService.EXTRA_MIN_RATE, 0.0f);
                mWorkout.setMinRate(minRate);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_STEP_COUNTS)) {
                ArrayList<Integer> stepCounts = intent.getIntegerArrayListExtra(
                        WorkoutService.EXTRA_STEP_COUNTS
                );
                mWorkout.setStepCounts(stepCounts);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_STEP_COUNTS_TIME)) {
                ArrayList<Long> stepCountsRecordTimes = NanoFitnessUtil
                        .convertLongArray(intent.getLongArrayExtra(
                                WorkoutService.EXTRA_STEP_COUNTS_TIME
                        ));
                mWorkout.setStepCountRecordTimes(stepCountsRecordTimes);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_LOCATIONS)) {
                ArrayList<Location> locations = intent
                        .getParcelableArrayListExtra(
                                WorkoutService.EXTRA_LOCATIONS
                        );
                mWorkout.setLocations(locations);
            }

            if (intent.hasExtra(WorkoutService.EXTRA_LOCATIONS_TIME)) {
                ArrayList<Long> locationsRecordTimes = NanoFitnessUtil
                        .convertLongArray(intent.getLongArrayExtra(
                                WorkoutService.EXTRA_LOCATIONS_TIME
                        ));
                mWorkout.setLocationRecordTimes(locationsRecordTimes);
            }

            final Workout workout = new Workout(mWorkout);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateUI(workout);
                }
            });
        }

    }

    private void updateUI(Workout workout) {
        updateDuration(workout.getSeconds());
        updateDistance(WorkoutMath.calculateDistanceInMiles(
                workout.getCurrentStepCount(),
                workout.isMale()
        ));
        updateRates(workout.getAvgRate(),
                workout.getMinRate(),
                workout.getMaxRate());
        updateGraphs(workout.getStepsPerMinute(),
                workout.getCaloriesBurned(mWeight),
                workout.getRatesRecordTimes());
        updateMap(workout.getLocations());
    }

    private void updateGraphs(List<Float> stepsPerMin,
                              List<Integer> caloriesBurned,
                              List<Long> recordTimes) {
        if (mLandscape != null) {
            mLandscape.updateGraphs(stepsPerMin,
                    caloriesBurned,
                    recordTimes);
        }
    }

    private void updateMap(List<Location> locations) {
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
        String gender = preferences.getString(ProfileActivity.GENDER_KEY, "male").toLowerCase();
        return gender.equals("male");
    }

    private void saveWorkout(Workout workout) {
        SaveWorkout thread = new SaveWorkout(
                getApplicationContext(),
                workout);
        thread.start();
    }


    private void styleActionbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.action_bar_record);
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }
}
