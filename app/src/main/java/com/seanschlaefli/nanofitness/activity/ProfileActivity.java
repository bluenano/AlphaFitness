package com.seanschlaefli.nanofitness.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.seanschlaefli.nanofitness.util.TimeFormatter;
import com.seanschlaefli.nanofitness.ProfileStats;
import com.seanschlaefli.nanofitness.R;
import com.seanschlaefli.nanofitness.model.Workout;
import com.seanschlaefli.nanofitness.viewmodel.WorkoutViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity
    implements OnWorkoutLoadComplete {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String EXTRA_WORKOUT_ID = "workout_id_extra";

    public static final String NAME_KEY = "name_key";
    public static final String GENDER_KEY = "gender_key";
    public static final String WEIGHT_KEY = "weight_key";

    public static final int DEFAULT_WEIGHT = 100;

    private WorkoutViewModel mWorkoutViewModel;

    private EditText mName;
    private EditText mGender;
    private EditText mWeight;

    private ProfileStats mAllTimeStats;
    private ProfileStats mAverageWeeklyStats;


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

        mWorkoutViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);

        Intent data = getIntent();
        if (data != null) {
            int workoutId = data.getIntExtra(EXTRA_WORKOUT_ID, -1);
        }

        mWorkoutViewModel.getAllWorkouts().observe(this, getWorkoutObserver());

        mAllTimeStats = new ProfileStats("All Time");
        mAverageWeeklyStats = new ProfileStats("Average/Weekly");

        Resources r = getResources();
        TextView titleAll = findViewById(R.id.title_text_view_id_1);
        TextView titleAvg = findViewById(R.id.title_text_view_id_2);
        titleAll.setText(r.getString(R.string.all_time));
        titleAvg.setText(r.getString(R.string.average_weekly));

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

        mName.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProfileInformation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileInformation();
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

    private Observer<List<Workout>> getWorkoutObserver() {
        return new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workouts) {

            }
        };
    }
    private void updateStats(ProfileStats stats, TextView distance, TextView time,
                             TextView workouts, TextView calories) {
        updateDistance(distance, stats.getDistance(), "mi");
        updateTime(time, stats.getTime());
        updateWorkouts(workouts, stats.getNumWorkouts());
        updateCalories(calories, stats.getNumCalories());
    }

    private void updateAllTimeStats() {
        /*
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
        */
    }

    private void updateAverageWeeklyStats() {
        /*
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
        */
    }

    private void updateDistance(TextView distanceTextView, float distance, String units) {
        String format = String.format(Locale.US, "%,.1f", distance);
        distanceTextView.setText(format + " " + units);
    }

    private void updateTime(TextView timeTextView, int timeInSeconds) {
        timeTextView.setText(
                TimeFormatter.createProfileTimeString(timeInSeconds));
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

    public static Intent newIntent(Context context, int workoutId) {
        Intent data = new Intent(context, ProfileActivity.class);
        data.putExtra(EXTRA_WORKOUT_ID, workoutId);
        return data;
    }

    private void styleActionbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.action_bar_profile);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onWorkoutLoadComplete(Integer id) {

    }

    private static class LoadWorkoutTask extends AsyncTask<Integer, Void, Void> {

        private WorkoutViewModel mVM;
        private int mWorkoutId;

        public LoadWorkoutTask(WorkoutViewModel vm, int workoutId) {
            mVM = vm;
            mWorkoutId = workoutId;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mVM.setCurrentWorkout(mWorkoutId);
            return null;
        }
    }

}
