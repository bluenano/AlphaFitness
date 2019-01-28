package com.seanschlaefli.nanofitness;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        super.onDestroy();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        if (mRecordRunnable != null) {
            mRecordRunnable.terminate();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        mWorkout = new Workout(mIsMale);
        mWorkout.setStartTime(mStartTime);
        SensorEventListener stepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
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
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };



        setLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mWorkout.addLocation(location, Calendar.getInstance().getTimeInMillis());
                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                List<Location> locations = locationResult.getLocations();
                if (locations.size() == 0) {
                    return;
                }
                for (Location location: locations) {
                    mWorkout.addLocation(location, Calendar.getInstance().getTimeInMillis());
                }
            }
        };
        startLocationUpdates();

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter != null) {
            sm.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

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
        intent.putExtra(EXTRA_STEP_COUNTS_TIME, NanoFitnessUtil.convertLongList(workout.getStepCountRecordTimes()));
        intent.putParcelableArrayListExtra(EXTRA_LOCATIONS, (ArrayList<Location>) workout.getLocations());
        intent.putExtra(EXTRA_LOCATIONS_TIME, NanoFitnessUtil.convertLongList(workout.getLocationRecordTimes()));
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
            mRunFlag = false;
        }

        @Override
        public void run() {
            while (mRunFlag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                mWorkout.incSeconds();
                broadcastWorkout(mWorkout);
            }
        }

    }


}
