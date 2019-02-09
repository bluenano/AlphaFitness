package com.seanschlaefli.nanofitness.service;

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
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seanschlaefli.nanofitness.database.dao.LocationRecordDao;
import com.seanschlaefli.nanofitness.database.dao.StepRecordDao;
import com.seanschlaefli.nanofitness.database.dao.WorkoutDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.database.entity.Workout;
import com.seanschlaefli.nanofitness.database.entity.StepRecord;
import com.seanschlaefli.nanofitness.database.entity.LocationRecord;
import com.seanschlaefli.nanofitness.util.WorkoutMath;

import java.util.Calendar;
import java.util.List;

public class WorkoutService extends Service implements OnStartRecordingWorkout {

    public static final String TAG = WorkoutService.class.getSimpleName();
    public static final String EXTRA_IS_MALE = "is_male";
    public static final String EXTRA_WORKOUT_ID = "new_workout_id";

    public static final int SENSOR_FOR_STEPS = Sensor.TYPE_STEP_COUNTER;

    private Workout mWorkout;
    private boolean mIsMale;

    private WorkoutDao mWorkoutDao;
    private StepRecordDao mStepRecordDao;
    private LocationRecordDao mLocationRecordDao;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private static RecordRunnable mRecordRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsMale = false;

        AppDatabase db = AppDatabase.getDatabase(this);
        mWorkoutDao = db.workoutDao();
        mStepRecordDao = db.stepRecordDao();
        mLocationRecordDao = db.locationRecordDao();

        int workoutId = -1;
        if (intent != null) {
            workoutId = intent.getIntExtra(EXTRA_WORKOUT_ID, workoutId);
            mIsMale = intent.getBooleanExtra(EXTRA_IS_MALE, mIsMale);
        }
        Log.d(TAG, "mWorkoutId is " + Integer.toString(workoutId));
        if (workoutId != -1) {
            new LoadWorkoutTask(this, mWorkoutDao).execute(workoutId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationClient != null && mLocationCallback != null) {
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

        setLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                ==
                PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                insertLocation(location);
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
                    for (Location location : locations) {
                        insertLocation(location);
                    }
                }
            };
        }
    }


    private void setLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
    }

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    null
            );
        } catch (SecurityException e) {} // find out best practice to handle this
    }

    private void insertStepRecord(int steps, long time) {
        StepRecord newSteps = new StepRecord(mWorkout.getId(),
                steps,
                time);
        mStepRecordDao.insert(newSteps);
    }

    private void insertLocation(Location location) {
        if (location != null && mWorkout != null) {
            LocationRecord newLocation = new LocationRecord(mWorkout.getId(),
                    location.getLatitude(),
                    location.getLongitude(),
                    Calendar.getInstance().getTimeInMillis(),
                    location.getProvider());
            mLocationRecordDao.insert(newLocation);
        }
    }

    private int getCurrentStepCount(int workoutId) {
        List<StepRecord> stepRecords = mStepRecordDao.loadByWorkoutId(workoutId).getValue();
        if (stepRecords != null) {
            int size = stepRecords.size();
            if (size > 1) {
                return stepRecords.get(size - 1).mRecordStep
                        - stepRecords.get(0).mRecordStep;
            }
        }
        return 0;
    }

    private SensorEventListener getSensorEventListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                float[] stepValues = event.values;

                if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    if (stepValues.length > 0) {
                        int stepsSinceReboot = (int) stepValues[0];
                        long time = Calendar.getInstance().getTimeInMillis();
                        insertStepRecord(stepsSinceReboot, time);
                        int steps = getCurrentStepCount(mWorkout.getId());
                        mWorkout.setStepCount(steps);
                        mWorkout.setDistance(WorkoutMath.calculateDistanceInMiles(steps, mIsMale));
                        List<StepRecord> records = mStepRecordDao.loadByWorkoutId(mWorkout.getId()).getValue();
                        if (records != null) {
                            mWorkout.updateRates(
                                    WorkoutMath.calculateAvgRateInMinPerMile(
                                            mWorkout.getStepCount(),
                                            mWorkout.getNumSeconds(),
                                            mIsMale),
                                    records.size()
                            );
                        }
                        mWorkoutDao.update(mWorkout);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    private void setLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    for (Location location : locations) {
                        insertLocation(location);
                    }
                }
            }
        };
    }

    private void handleFailure() {

    }

    @Override
    public void onStartRecordingWorkout(Workout workout) {
        mWorkout = workout;
        setLocationCallback();
        startLocationUpdates();
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounter = sm.getDefaultSensor(SENSOR_FOR_STEPS);
        if (stepCounter != null) {
            sm.registerListener(getSensorEventListener(), stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
        mRecordRunnable = new RecordRunnable();
        Thread thread = new Thread(mRecordRunnable);
        thread.start();
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
                Log.d(TAG, "Service updating workout with id " + Integer.toString(mWorkout.getId()));
                mWorkout.incNumSeconds();
                mWorkoutDao.update(mWorkout);
            }
        }

    }

    private static class LoadWorkoutTask extends AsyncTask<Integer, Integer, Workout> {

        private OnStartRecordingWorkout mCallback;
        private WorkoutDao mWorkoutDao;

        public LoadWorkoutTask(OnStartRecordingWorkout callback, WorkoutDao workoutDao) {
            mCallback = callback;
            mWorkoutDao = workoutDao;
        }

        @Override
        protected Workout doInBackground(Integer... integers) {
            if (integers.length == 1) {
                return mWorkoutDao.loadByIdSync(integers[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Workout workout) {
            if (workout != null) {
                mCallback.onStartRecordingWorkout(workout);
            }
            super.onPostExecute(workout);
        }
    }


}
