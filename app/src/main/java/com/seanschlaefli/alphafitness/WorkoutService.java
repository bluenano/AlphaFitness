package com.seanschlaefli.alphafitness;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class WorkoutService extends Service {

    public static final String TAG = WorkoutService.class.getSimpleName();
    public static final String EXTRA_IS_MALE = "is_male";
    public static final String EXTRA_START_TIME = "start_time";
    public static final String EXTRA_STEP_COUNT = "step_count";
    public static final String EXTRA_AVG_RATE = "average_rate";
    public static final String EXTRA_MIN_RATE = "minimum_rate";
    public static final String EXTRA_MAX_RATE = "maximum_rate";
    public static final String EXTRA_TOTAL_TIME = "total_time";
    public static final String EXTRA_STEP_COUNTS = "step_counts";
    public static final String EXTRA_STEP_COUNTS_TIME = "step_counts_time";
    public static final String EXTRA_LOCATIONS = "locations";
    public static final String EXTRA_LOCATIONS_TIME = "locations_time";

    public Context mContext;

    private Workout mWorkout;
    private boolean mIsMale;
    private long mStartTime;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private RecordRunnable mRecordRunnable;
    private WorkoutSimulationTask mSimulation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsMale = true;
        mStartTime = Calendar.getInstance().getTimeInMillis();
        if (intent != null) {
            mIsMale = intent.getBooleanExtra(EXTRA_IS_MALE, true);
            mStartTime = intent.getLongExtra(EXTRA_START_TIME, mStartTime);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        if (mSimulation != null) {
            mSimulation.terminate();
        }
        if (mRecordRunnable != null) {
            mRecordRunnable.terminate();
        }
        Log.d(TAG, "Workout Service onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Remote Service Created");

        mContext = this;

        mWorkout = new Workout(mIsMale);
        mWorkout.setStartTime(mStartTime);
        SensorEventListener stepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(TAG, "Sensor Event Detected");

                Sensor sensor = event.sensor;
                float[] stepValues = event.values;

                if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    if (stepValues.length > 0) {
                        int stepsSinceReboot = (int) stepValues[0];
                        long time = Calendar.getInstance().getTimeInMillis();
                        mWorkout.addStepCount(stepsSinceReboot, time);
                        mWorkout.updateRates(WorkoutMath.calculateAvgRateInMinPerMile(mWorkout.getCurrentStepCount(),
                                mWorkout.getTotalTime(), mWorkout.isMale()));
                    }
                    Log.d(TAG, "Step Count detected and incremented to " + Integer.toString(mWorkout.getCurrentStepCount()));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };


        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter == null) {
            Log.d(TAG, "Device does not have a step counter");
            mSimulation = new WorkoutSimulationTask();
            Thread thread = new Thread(mSimulation);
            thread.start();
            return;
        } else {
            Log.d(TAG, "Device does have a step counter");
        }


        setLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Do not have the appropriate permissions");
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "First current location set");
                            mWorkout.addLocation(location, Calendar.getInstance().getTimeInMillis());
                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "Received location callback");
                if (locationResult == null) {
                    return;
                }
                List<Location> locations = locationResult.getLocations();
                if (locations.size() == 0) {
                    return;
                }
                Log.d(TAG, "LocationResult size is " + Integer.toString(locations.size()));
                for (Location location: locations) {
                    mWorkout.addLocation(location, Calendar.getInstance().getTimeInMillis());
                }
            }
        };

        startLocationUpdates();
        sm.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mRecordRunnable = new RecordRunnable();
        Thread thread = new Thread(mRecordRunnable);
        thread.start();

    }


    private void broadcastWorkout(Workout workout) {
        Intent intent = new Intent("com.seanschlaefli.alphafitness.CUSTOM_INTENT");
        intent.putExtra(EXTRA_STEP_COUNT, workout.getCurrentStepCount());
        intent.putExtra(EXTRA_TOTAL_TIME, workout.getSeconds());
        intent.putExtra(EXTRA_AVG_RATE, workout.getAvgRate());
        intent.putExtra(EXTRA_MAX_RATE, workout.getMaxRate());
        intent.putExtra(EXTRA_MIN_RATE, workout.getMinRate());
        intent.putIntegerArrayListExtra(EXTRA_STEP_COUNTS, (ArrayList<Integer>) workout.getStepCounts());
        intent.putExtra(EXTRA_STEP_COUNTS_TIME, AlphaFitnessUtil.convertLongList(workout.getStepCountRecordTimes()));
        intent.putParcelableArrayListExtra(EXTRA_LOCATIONS, (ArrayList<Location>) workout.getLocations());
        intent.putExtra(EXTRA_LOCATIONS_TIME, AlphaFitnessUtil.convertLongList(workout.getLocationRecordTimes()));
        sendBroadcast(intent);

    }


    private void setLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Do not have the appropriate permissions");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                null
        );
    }


    public class RecordRunnable implements Runnable {

        private volatile boolean mRunFlag = true;

        public void terminate() {
            Log.d(TAG, "Terminating record workout thread");
            mRunFlag = false;
        }

        @Override
        public void run() {
            while (mRunFlag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d(TAG, "InterruptedException in location tracking loop");
                }
                mWorkout.incSeconds();
                broadcastWorkout(mWorkout);
            }
        }

    }


    public class WorkoutSimulationTask implements Runnable {

        private int mStepCount = 0;
        private Random mRand = new Random();
        private volatile boolean mRunFlag = true;

        @Override
        public void run() {
            mStepCount = (int) generateFloat(0.5f, 1000.0f);
            mWorkout.addStepCount(mStepCount, Calendar.getInstance().getTimeInMillis());
            while (mRunFlag) {
                float current = generateFloat(3.5f, 4.0f);
                mStepCount += (int) current;
                mWorkout.addStepCount(mStepCount, Calendar.getInstance().getTimeInMillis());
                mWorkout.updateRates(WorkoutMath.calculateAvgRateInMinPerMile(mWorkout.getCurrentStepCount(),
                        mWorkout.getTotalTime(), mWorkout.isMale()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                mWorkout.incSeconds();
                broadcastWorkout(mWorkout);
            }
        }

        public void terminate() {
            mRunFlag = false;
        }


        public float generateFloat(float min, float max) {
            return mRand.nextFloat() * (max - min) + min;
        }

    }

}
