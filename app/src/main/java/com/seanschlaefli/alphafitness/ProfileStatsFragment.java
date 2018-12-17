package com.seanschlaefli.alphafitness;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

public class ProfileStatsFragment extends Fragment {

    public static final String TAG = ProfileStatsFragment.class.getSimpleName();
    public static final String ARG_TITLE = "com.seanschlaefli.alphafitness.TITLE";

    private static final int SECONDS_IN_DAY = 86400;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;

    private TextView mTitle;
    private TextView mDistance;
    private TextView mTime;
    private TextView mWorkouts;
    private TextView mCalories;

    public ProfileStatsFragment() {}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workout_stats, container, false);

        mTitle = v.findViewById(R.id.title_text_view_id);
        mDistance = v.findViewById(R.id.distance_text_view_id);
        mTime = v.findViewById(R.id.time_text_view_id);
        mWorkouts = v.findViewById(R.id.workouts_text_view_id);
        mCalories = v.findViewById(R.id.calories_text_view_id);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            mTitle.setText(title);
        }

        return v;
    }

    public void updateDistance(float newDistance, String units) {
        String format = String.format("%,.1f", newDistance);
        mDistance.setText(format + " " + units);
    }

    public void updateTime(int timeInSeconds) {
        mTime.setText(createTimeString(timeInSeconds));
    }

    public void updateNumWorkouts(int newNumWorkouts) {
        String formatted = NumberFormat.getInstance().format(newNumWorkouts);
        mWorkouts.setText(formatted + " times");
    }

    public void updateCaloriesBurned(int newCaloriesBurned) {
        String formatted = NumberFormat.getInstance().format(newCaloriesBurned);
        mCalories.setText(formatted + " Cal");
    }

    private String createTimeString(int timeInSeconds) {
        Log.d(TAG, "Time String " + Long.toString(timeInSeconds));
        String daysStr = "";
        String hoursStr = "";
        String minutesStr = "";
        int days = getNumDays(timeInSeconds);
        if (days > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_DAY;
            daysStr = Integer.toString(days) + " day ";
        }
        int hours = getNumHours(timeInSeconds);
        if (hours > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_HOUR;
            hoursStr = Integer.toString(hours) + " hr ";
        }
        int minutes = getNumMinutes(timeInSeconds);
        if (minutes > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_MINUTE;
            minutesStr = Integer.toString(minutes) + " min ";
        }
        String secondsStr = Integer.toString(timeInSeconds) + " sec ";
        return daysStr + hoursStr + minutesStr + secondsStr;
    }

    private int getNumDays(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_DAY;
    }

    private int getNumHours(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_HOUR;
    }

    private int getNumMinutes(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_MINUTE;
    }

    public static ProfileStatsFragment newInstance() {
        return new ProfileStatsFragment();
    }
}
