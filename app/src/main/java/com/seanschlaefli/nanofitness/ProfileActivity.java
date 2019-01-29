package com.seanschlaefli.nanofitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String EXTRA_IS_RECORDING = "is_recording";
    public static final String EXTRA_ADD_STEPS = "step_count";
    public static final String EXTRA_ADD_TIME = "total_time";
    public static final String NAME_KEY = "name_key";
    public static final String GENDER_KEY = "gender_key";
    public static final String WEIGHT_KEY = "weight_key";

    public static final int DEFAULT_WEIGHT = 100;

    private WorkoutHistory mWorkoutHistory;

    private EditText mName;
    private EditText mGender;
    private EditText mWeight;

    private ProfileStats mAllTimeStats;
    private ProfileStats mAverageWeeklyStats;

    private HandlerThread mHandlerThread;
    private Handler mBroadcastHandler;
    private Handler mMainHandler;
    private BroadcastReceiver mReceiver;

    private boolean mIsWorkoutStarted;
    private int mAddSteps;
    private int mAddTime;

    private TextView mAllDistance;
    private TextView mAllTime;
    private TextView mAllWorkouts;
    private TextView mAllCalories;

    private TextView mAvgDistance;
    private TextView mAvgTime;
    private TextView mAvgWorkouts;
    private TextView mAvgCalories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        styleActionbar();

        Intent data = getIntent();
        if (data != null) {
            mIsWorkoutStarted  = data.getBooleanExtra(EXTRA_IS_RECORDING, false);
            mAddSteps = data.getIntExtra(EXTRA_ADD_STEPS, 0);
            mAddTime = data.getIntExtra(EXTRA_ADD_TIME, 0);
            initializeHandlers();
        } else {
            mIsWorkoutStarted = false;
            mAddSteps = 0;
            mAddTime = 0;
        }

        mAllTimeStats = new ProfileStats("All Time");
        mAverageWeeklyStats = new ProfileStats("Average/Weekly");

        Resources r = getResources();
        TextView titleAll = findViewById(R.id.title_text_view_id_1);
        TextView titleAvg = findViewById(R.id.title_text_view_id_2);
        titleAll.setText(r.getString(R.string.all_time));
        titleAvg.setText(r.getString(R.string.average_weekly));

        getSupportLoaderManager().initLoader(0, null, this);

        mName = findViewById(R.id.name_edit_text_id);
        mGender = findViewById(R.id.gender_edit_text_id);
        mWeight = findViewById(R.id.weight_edit_text_id);
        mAllDistance = findViewById(R.id.distance_text_view_id_1);
        mAllTime = findViewById(R.id.time_text_view_id_1);
        mAllWorkouts = findViewById(R.id.workouts_text_view_id_1);
        mAllCalories = findViewById(R.id.calories_text_view_id_1);
        mAvgDistance = findViewById(R.id.distance_text_view_id_2);
        mAvgTime = findViewById(R.id.time_text_view_id_2);
        mAvgWorkouts = findViewById(R.id.workouts_text_view_id_2);
        mAvgCalories = findViewById(R.id.calories_text_view_id_2);

        initializeHandlers();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleWorkoutBroadcast(intent);
            }
        };
        mName.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProfileInformation();
        this.unregisterReceiver(mReceiver);
        if (isFinishing()) {
            mHandlerThread.quit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileInformation();
        IntentFilter intentFilter = new IntentFilter("com.seanschlaefli.alphafitness.CUSTOM_INTENT");
        this.registerReceiver(mReceiver, intentFilter, null, mBroadcastHandler);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveProfileInformation() {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(
                "ProfilePreferences",
                MODE_PRIVATE
        ).edit();
        editor.putString(NAME_KEY, mName.getText().toString());
        String gender = mGender.getText().toString();
        if (validateGender(gender)) {
            editor.putString(GENDER_KEY, gender);
        }
        String weight = mWeight.getText().toString();
        if (validateWeight(weight)) {
            editor.putString(WEIGHT_KEY, weight);
        }
        editor.apply();
    }

    private void loadProfileInformation() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "ProfilePreferences",
                MODE_PRIVATE
        );
        mName.setText(preferences.getString(NAME_KEY, ""));
        mGender.setText(preferences.getString(GENDER_KEY, ""));
        mWeight.setText(preferences.getString(WEIGHT_KEY, ""));
    }



    private boolean validateGender(String gender) {
        gender = gender.toLowerCase();
        return (gender.equals("male") ||
                gender.equals("female"));
    }

    private static boolean validateWeight(String weight) {
        try {
            Integer.parseInt(weight);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void updateStats(ProfileStats stats, TextView distance, TextView time,
                             TextView workouts, TextView calories) {
        updateDistance(distance, stats.getDistance(), "mi");
        updateTime(time, stats.getTime());
        updateWorkouts(workouts, stats.getNumWorkouts());
        updateCalories(calories, stats.getNumCalories());
    }

    private void updateAllTimeStats() {
        if (mWorkoutHistory != null) {
            int totalSteps = mWorkoutHistory.getTotalSteps() + mAddSteps;
            updateStatsModel(mAllTimeStats,
                    WorkoutMath.calculateDistanceInMiles(totalSteps,
                            mWorkoutHistory.isMale()),
                    mWorkoutHistory.getTotalTime(),
                    mWorkoutHistory.getNumWorkouts(),
                    WorkoutMath.calculateCaloriesBurned(totalSteps,
                            getWeight(mWeight.getText().toString())));
            updateStats(mAllTimeStats, mAllDistance, mAllTime,
                    mAllWorkouts, mAllCalories);
        }
    }

    private void updateAverageWeeklyStats() {
        if (mWorkoutHistory != null) {
            long totalTime = mWorkoutHistory.getTotalTime() + mAddTime;
            int totalSteps = mWorkoutHistory.getTotalSteps() + mAddSteps;
            if (WorkoutMath.isLongerThanAWeek(totalTime)) {
                float numWeeks = mWorkoutHistory.getNumWeeks();
                float totalDistanceInMiles = WorkoutMath.calculateDistanceInMiles(totalSteps,
                        mWorkoutHistory.isMale());
                int caloriesBurned = WorkoutMath.calculateCaloriesBurned(
                        totalSteps,
                        getWeight(mWeight.getText().toString()));
                float averageWeeklyDistance = totalDistanceInMiles / numWeeks;
                int avgWeeklyTime =  (int) (totalTime / numWeeks);
                int avgWeeklyWorkouts = (int) (mWorkoutHistory.getNumWorkouts() / numWeeks);
                int avgWeeklyCal = (int) (caloriesBurned / numWeeks);
                if (mAverageWeeklyStats == null) {
                    mAverageWeeklyStats = new ProfileStats("Average/Weekly");
                }
                updateStatsModel(mAverageWeeklyStats, averageWeeklyDistance,
                        avgWeeklyTime, avgWeeklyWorkouts, avgWeeklyCal);
                updateStats(mAverageWeeklyStats, mAvgDistance, mAvgTime,
                        mAvgWorkouts, mAvgCalories);
            } else {
                updateStats(mAllTimeStats, mAvgDistance, mAvgTime,
                        mAvgWorkouts, mAvgCalories);
            }
        }
    }

    private void updateDistance(TextView distanceTextView, float distance, String units) {
        String format = String.format(Locale.US, "%,.1f", distance);
        distanceTextView.setText(format + " " + units);
    }

    private void updateTime(TextView timeTextView, int timeInSeconds) {
        timeTextView.setText(
                NanoFitnessUtil.createTimeStringFromSeconds(timeInSeconds));
    }

    private void updateWorkouts(TextView workoutsTextView, int numWorkouts) {
        String formatted = NumberFormat.getInstance().format(numWorkouts);
        workoutsTextView.setText(formatted + " times");
    }

    private void updateCalories(TextView caloriesTextView, int calories) {
        String formatted = NumberFormat.getInstance().format(calories);
        caloriesTextView.setText(formatted + " Cal");
    }

    private void updateStatsModel(ProfileStats stats, float newDistance,
                                  int newTime, int newWorkouts, int newCalories) {
        stats.setDistance(newDistance);
        stats.setTime(newTime);
        stats.setNumWorkouts(newWorkouts);
        stats.setNumCalories(newCalories);
    }

    public static int getWeight(String weightStr) {
        if (validateWeight(weightStr)) {
            return Integer.parseInt(weightStr);
        }
        return DEFAULT_WEIGHT;
    }

    private boolean isMale(String gender) {
        gender = gender.toLowerCase();
        return gender.equals("male");
    }

    private void handleWorkoutBroadcast(Intent intent) {
        if (intent.hasExtra(WorkoutService.EXTRA_STEP_COUNT)) {
            mAddSteps = intent.getIntExtra(WorkoutService.EXTRA_STEP_COUNT, 0);
        }

        if (intent.hasExtra(WorkoutService.EXTRA_TOTAL_TIME)) {
            mAddTime = intent.getIntExtra(WorkoutService.EXTRA_TOTAL_TIME, 0);
        }

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                updateAllTimeStats();
            }
        });

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                updateAverageWeeklyStats();
            }
        });
    }

    public static Intent newIntent(Context context, boolean isRecording,
                                   int stepCount, int totalTime) {
        Intent data = new Intent(context, ProfileActivity.class);
        data.putExtra(EXTRA_IS_RECORDING, isRecording);
        data.putExtra(EXTRA_ADD_STEPS, stepCount);
        data.putExtra(EXTRA_ADD_TIME, totalTime);
        return data;
    }

    private void initializeHandlers() {
        mHandlerThread = new HandlerThread("Broadcast Handler Thread");
        mHandlerThread.start();
        mBroadcastHandler = new Handler(mHandlerThread.getLooper());
        mMainHandler = new Handler();
    }

    private void styleActionbar() {
        ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(R.layout.action_bar_profile);
        ab.setCustomView(R.layout.action_bar_profile);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                this,
                FitnessContentProvider.URI_WORKOUT,
                null,
                null,
                null,
                FitnessContentProvider.START_TIME + " ASC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mWorkoutHistory = WorkoutHistoryFactory.from(data, isMale(mGender.getText().toString()));
        if (mIsWorkoutStarted) {
            mWorkoutHistory.incNumWorkouts();
        }
        Log.d(TAG, "Workout History Total Time " + Long.toString(mWorkoutHistory.getTotalTime()));
        updateAllTimeStats();
        updateAverageWeeklyStats();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

}
