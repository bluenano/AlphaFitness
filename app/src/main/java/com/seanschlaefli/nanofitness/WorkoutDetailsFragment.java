package com.seanschlaefli.nanofitness;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailsFragment extends Fragment {

    public static final String TAG = WorkoutDetailsFragment.class.getSimpleName();
    public static final String START_TIME_KEY = "start_time_key";
    public static final String AVG_RATE_KEY = "avg_rate_key";
    public static final String MAX_RATE_KEY = "max_rate_key";
    public static final String MIN_RATE_KEY = "min_rate_key";
    public static final String STEPS_PER_MIN_KEY = "steps_per_min_key";
    public static final String CALORIES_BURNED_KEY = "calories_burned_key";
    public static final String RATES_RECORD_TIMES_KEY = "rates_record_times_key";

    private static final int SCALAR = 5;
    private static final String BAR_CHART_DESC = "Calories Burned";
    private static final String LINE_CHART_DESC = "Steps Per " +
            Integer.toString(SCALAR) + " Minutes";


    private TextView mAvgRate;
    private TextView mMaxRate;
    private TextView mMinRate;

    private BarChart mBarChart;
    private LineChart mLineChart;

    private BarDataSet mBarData;
    private LineDataSet mLineData;

    private long mStartTime;

    private List<Integer> mXAxisMinutes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workout_details
                , container, false);

        mAvgRate = v.findViewById(R.id.average_text_view_id);
        mMaxRate = v.findViewById(R.id.max_text_view_id);
        mMinRate = v.findViewById(R.id.min_text_view_id);

        mBarChart = v.findViewById(R.id.bar_chart_id);
        mLineChart = v.findViewById(R.id.line_chart_id);

        initializeXAxisValues(5, 60, 5);
        setupXAxis(mBarChart);
        setupXAxis(mLineChart);

        List<Float> stepsPerMin = new ArrayList<>();
        List<Integer> caloriesBurned = new ArrayList<>();
        List<Long> ratesRecordTimes = new ArrayList<>();
        Bundle args = getArguments();
        if (args != null) {
            mStartTime = args.getLong(START_TIME_KEY);
            float[] stepsPerMinArr = args.getFloatArray(STEPS_PER_MIN_KEY);
            int[] caloriesArr = args.getIntArray(CALORIES_BURNED_KEY);
            long[] ratesRecordTimesArr = args.getLongArray(RATES_RECORD_TIMES_KEY);
            if (stepsPerMinArr != null &&
                    caloriesArr != null &&
                    ratesRecordTimesArr != null) {
                stepsPerMin = NanoFitnessUtil.convertFloatArray(stepsPerMinArr);
                caloriesBurned = NanoFitnessUtil.convertIntArray(caloriesArr);
                ratesRecordTimes = NanoFitnessUtil.convertLongArray(ratesRecordTimesArr);
            }
            updateAvgRate(args.getFloat(AVG_RATE_KEY));
            updateMaxRate(args.getFloat(MAX_RATE_KEY));
            updateMinRate(args.getFloat(MIN_RATE_KEY));
        }

        initializeBarData(caloriesBurned, ratesRecordTimes, mStartTime);
        initializeLineData(stepsPerMin, ratesRecordTimes, mStartTime, SCALAR);
        Log.d(TAG, mBarData.toString());
        Log.d(TAG, mLineData.toString());

        return v;
    }

    public void updateAvgRate(float newRate) {
        mAvgRate.setText(createTimeString(newRate));
    }

    public void updateMinRate(float newRate) {
        mMinRate.setText(createTimeString(newRate));
    }

    public void updateMaxRate(float newRate) {
        mMaxRate.setText(createTimeString(newRate));
    }

    public void updateGraphs(List<Float> stepsPerMin,
                             List<Integer> caloriesBurned,
                             List<Long> recordTimes) {
        initializeBarData(caloriesBurned, recordTimes, mStartTime);
        initializeLineData(stepsPerMin, recordTimes, mStartTime, SCALAR);
    }

    public static WorkoutDetailsFragment newInstance() {
        return new WorkoutDetailsFragment();
    }

    private void initializeXAxisValues(int min, int max, int increment) {
        mXAxisMinutes = new ArrayList<>();
        for (int i = min; i < max; i += increment) {
            mXAxisMinutes.add(i);
        }
    }

    private void setupXAxis(Chart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(mXAxisMinutes.get(mXAxisMinutes.size() - 1));

    }

    private void initializeBarData(List<Integer> caloriesBurned,
                                   List<Long> recordTimes,
                                   long startTime) {
        int s1 = caloriesBurned.size();
        int s2 = recordTimes.size();
        List<BarEntry> values = new ArrayList<>();
        if (s1 == s2) {
            for (int i = 0; i < s1; i++) {
                int calories = caloriesBurned.get(i);
                float timeInMinutes = UnitConverter.msToMinutes(recordTimes.get(i) - startTime);
                values.add(new BarEntry(timeInMinutes, calories));
            }
        }
        mBarData = new BarDataSet(values, "Calories Burned");
        setBarChartData();
    }

    private void initializeLineData(List<Float> stepsPerMin,
                                    List<Long> recordTimes,
                                    long startTime,
                                    int scalar) {
        int s1 = stepsPerMin.size();
        int s2 = recordTimes.size();
        List<Entry> values = new ArrayList<>();
        if (s1 == s2 && s1 > 1) {
            for (int i = 1; i < s1; i++) {
                float rate = scalar * stepsPerMin.get(i);
                float timeInMinutes = UnitConverter.msToMinutes(recordTimes.get(i) - startTime);
                values.add(new Entry(timeInMinutes, rate));
            }

        }
        mLineData = new LineDataSet(values, "Steps Per " +
                Integer.toString(scalar) + " Minutes");
        setLineChartData();
    }

    private void setBarChartData() {
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(mBarData);
        BarData data = new BarData(dataSets);
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getDescription().setText(BAR_CHART_DESC);
        mBarChart.setData(data);
        mBarChart.invalidate();
    }


    private void setLineChartData() {
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineData);
        LineData data = new LineData(dataSets);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getDescription().setText(LINE_CHART_DESC);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    private String createTimeString(float rate) {
        double decimal = rate - Math.floor(rate);
        int seconds = (int) (decimal * 60);
        String format = String.format("%d:%02d", (int) rate, seconds);
        return format;
    }
}
