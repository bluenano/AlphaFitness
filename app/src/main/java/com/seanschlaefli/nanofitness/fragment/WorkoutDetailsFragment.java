package com.seanschlaefli.nanofitness.fragment;

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
import com.seanschlaefli.nanofitness.util.TimeFormatter;
import com.seanschlaefli.nanofitness.R;
import com.seanschlaefli.nanofitness.util.UnitConverter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailsFragment extends Fragment {

    public static final String TAG = WorkoutDetailsFragment.class.getSimpleName();

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

    private List<Integer> mXAxisMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        return v;
    }

    public void updateAvgRate(float newRate) {
        mAvgRate.setText(
                TimeFormatter.createTimeStringFromRate(newRate));
    }

    public void updateMinRate(float newRate) {
        mMinRate.setText(
                TimeFormatter.createTimeStringFromRate(newRate));
    }

    public void updateMaxRate(float newRate) {
        mMaxRate.setText(
                TimeFormatter.createTimeStringFromRate(newRate));
    }

    public void updateGraphs(long startTime,
                             List<Float> stepsPerMin,
                             List<Integer> caloriesBurned,
                             List<Long> recordTimes) {
        initializeBarData(caloriesBurned, recordTimes, startTime);
        initializeLineData(stepsPerMin, recordTimes, startTime, SCALAR);
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
        mBarData = new BarDataSet(values, getResources().getString(R.string.bar_graph_label));
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
        // Can't get this to work after migrating to AndroidX
       /* mLineData = new LineDataSet(values,
                getResources().getQuantityString(R.plurals.line_chart_label,
                        SCALAR,
                        SCALAR));*/
        mLineData = new LineDataSet(values,
                "Steps Per " + Integer.toString(SCALAR) + " Minutes");
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
}
