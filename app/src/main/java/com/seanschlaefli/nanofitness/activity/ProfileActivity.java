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

import com.seanschlaefli.nanofitness.activity.model.ProfileStatsFactory;
import com.seanschlaefli.nanofitness.util.TimeFormatter;
import com.seanschlaefli.nanofitness.activity.model.ProfileStats;
import com.seanschlaefli.nanofitness.R;
import com.seanschlaefli.nanofitness.database.entity.Workout;
import com.seanschlaefli.nanofitness.viewmodel.WorkoutViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity
    implements OnProfileStatsLoadComplete {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String NAME_KEY = "name_key";
    public static final String GENDER_KEY = "gender_key";
    public static final String WEIGHT_KEY = "weight_key";

    public static final int DEFAULT_WEIGHT = 100;

    private WorkoutViewModel mWorkoutViewModel;

    private EditText mName;
    private EditText mGender;
    private EditText mWeight;

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
        mWorkoutViewModel.getAllWorkouts().observe(this,
                getWorkoutsObserver(this, getWeight(mWeight.getText().toString())));
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

    private Observer<List<Workout>> getWorkoutsObserver(final OnProfileStatsLoadComplete callback,
                                                        final int weight) {
        return new Observer<List<Workout>>() {
            @Override
            public final void onChanged(List<Workout> workouts) {
                new CalculateStatsTask(callback, weight).execute(workouts);
            }
        };
    }
    private void updateStats(ProfileStats stats, TextView distance,
                             TextView time, TextView workouts, TextView calories) {
        updateDistance(distance, stats.getDistance(), "mi");
        updateTime(time, stats.getTime());
        updateWorkouts(workouts, stats.getNumWorkouts());
        updateCalories(calories, stats.getNumCalories());
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

    public static int getWeight(String weightStr) {
        if (validateWeight(weightStr)) {
            return Integer.parseInt(weightStr);
        }
        return DEFAULT_WEIGHT;
    }

    public static Intent newIntent(Context context) {
        Intent data = new Intent(context, ProfileActivity.class);
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
    public void onProfileStatsLoadComplete(ProfileStats allTime, ProfileStats avgWeekly) {
        updateStats(allTime, mAllDistance, mAllTime, mAllWorkouts, mAllCalories);
        updateStats(avgWeekly, mAvgDistance, mAvgTime, mAvgWorkouts, mAvgCalories);
    }

    private static class CalculateStatsTask extends AsyncTask<List<Workout>, Void, List<ProfileStats>> {

        private OnProfileStatsLoadComplete mCallback;
        private int mWeight;

        public CalculateStatsTask(OnProfileStatsLoadComplete callback, int weight) {
            mCallback = callback;
            mWeight = weight;
        }

        @SafeVarargs
        @Override
        protected final List<ProfileStats> doInBackground(List<Workout>... lists) {
            if (lists.length == 1) {
                ProfileStats allTime = ProfileStatsFactory.createAllTime(lists[0], mWeight);
                ProfileStats avgWeekly = ProfileStatsFactory.createAverageWeekly(lists[0], mWeight);
                List<ProfileStats> ret = new ArrayList<>();
                ret.add(allTime);
                ret.add(avgWeekly);
                return ret;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProfileStats> profileStats) {
            if (profileStats != null && profileStats.size() == 2) {
                mCallback.onProfileStatsLoadComplete(profileStats.get(0),
                        profileStats.get(1));
            }
            super.onPostExecute(profileStats);
        }
    }


}
