package com.seanschlaefli.nanofitness.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seanschlaefli.nanofitness.util.TimeFormatter;
import com.seanschlaefli.nanofitness.util.PermissionManager;
import com.seanschlaefli.nanofitness.R;
import com.seanschlaefli.nanofitness.service.WorkoutService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecordWorkoutFragment extends Fragment {

    public static final String TAG = RecordWorkoutFragment.class.getSimpleName();
    public static final String RECORD_KEY = "is_workout_started_key";

    private static final int DEFAULT_ZOOM = 15;
    private static final int DEFAULT_CIRCLE_SIZE = 10;
    private static final int START_MARK_COLOR = Color.BLACK;
    private static final int CURRENT_MARK_COLOR = Color.RED;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private Button mRecordButton;
    private TextView mDistance;
    private TextView mDuration;

    private boolean mIsRecording;

    private OnRecordChange mCallback;
    private Context mContext;

    private Circle mStartingLocation;
    private Circle mCurrentLocation;
    private Polyline mRoute;

    public interface OnRecordChange {
        void workoutStarted(long startTime);
        void workoutEnded(long endTime);
        void startProfileActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "In RecordWorkoutFragment onCreateView");
        View v = inflater.inflate(R.layout.fragment_workout, container, false);

        ImageButton profileButton = v.findViewById(R.id.profile_button_id);
        mRecordButton = v.findViewById(R.id.record_button_id);
        mDistance = v.findViewById(R.id.distance_id);
        mDuration = v.findViewById(R.id.duration_id);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.startProfileActivity();
            }
        });

        float distance = 0.0f;
        int seconds = 0;
        Bundle args = getArguments();
        if (args != null) {
            mIsRecording = args.getBoolean(RECORD_KEY);
        } else {
            mIsRecording = false;
        }

        setRecordText();
        updateDistance(distance);
        updateDuration(seconds);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRecordStateChange();
            }
        });

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
        }
        loadMap();

        return v;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int current = getResources().getConfiguration().orientation;
        if (current == Configuration.ORIENTATION_PORTRAIT) {
            loadMap();
        }
    }

    public static RecordWorkoutFragment newInstance() {
        return new RecordWorkoutFragment();
    }

    private void loadMap() {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            fm.beginTransaction().add(R.id.map_id, mMapFragment).commit();
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    if (!mIsRecording) {
                        displayDefaultMap();
                    }
                }
            });
        }
    }

    private void handleRecordStateChange() {
        if (mIsRecording) {
            mIsRecording = false;
            mCallback.workoutEnded(Calendar.getInstance().getTimeInMillis());
            mRecordButton.setText(getResources().getString(R.string.start_workout));
        } else {
            resetMap();
            Activity activity = getActivity();
            if (activity != null) {
                mIsRecording = true;
                mCallback.workoutStarted(Calendar.getInstance().getTimeInMillis());
                mRecordButton.setText(getResources().getString(R.string.stop_workout));
                initializeMap();
                /*
                boolean locationPermission = hasLocationPermission();
                boolean sensorRequired = hasRequiredSensor();
                if (locationPermission && sensorRequired) {
                    mIsRecording = true;
                    mCallback.workoutStarted(Calendar.getInstance().getTimeInMillis());
                    mRecordButton.setText(getResources().getString(R.string.stop_workout));
                    initializeMap();
                } else {
                    displayToast(locationPermission, sensorRequired);
                }
                */
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnRecordChange) context;
        mContext = context;
    }

    private boolean hasLocationPermission() {
        Activity activity = getActivity();
        if (activity != null) {
            PermissionManager manager = new PermissionManager(activity);
            return manager.hasLocationPermission();
        }
        return false;
    }

    private boolean hasRequiredSensor() {
        Activity activity = getActivity();
        if (activity != null) {
            SensorManager sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
            Sensor stepCounter = sm.getDefaultSensor(WorkoutService.SENSOR_FOR_STEPS);
            return stepCounter != null;
        }
        return false;
    }

    public void updateDistance(float distance) {
        mDistance.setText(String.format(Locale.US, "%.1f", distance));
    }

    public void updateDuration(int duration) {
        mDuration.setText(
                TimeFormatter.createWorkoutTimeString(duration));
    }

    private void setRecordText() {
        if (mIsRecording) {
            mRecordButton.setText(getResources().getString(R.string.stop_workout));
        } else {
            mRecordButton.setText(getResources().getString(R.string.start_workout));
        }
    }


    private void initializeMap() {
        /*
        int size = mLocations.size();
        if (mIsRecording && size > 0) {
            showRoute();
        } else {
            displayDefaultMap();
        }
        */
    }

    private void resetMap() {
        mMap.clear();
        mStartingLocation = null;
        mCurrentLocation = null;
        mRoute = null;
    }

    public void updateMap(List<Location> locations) {
        int size = locations.size();
        if (size > 0) {
            Location start = locations.get(0);
            Location end = locations.get(size-1);
            setStartingLocation(new LatLng(start.getLatitude(),
                    start.getLongitude()));
            setCurrentLocation(new LatLng(end.getLatitude(),
                    end.getLongitude()));
            showRoute(locations);
        }
    }

    private void showRoute(List<Location> locations) {
        ArrayList<LatLng> routePoints = getRoutePoints(locations);
        mRoute = mMap.addPolyline(new PolylineOptions()
                .addAll(routePoints));
        mRoute.setTag("Route");
    }

    private CircleOptions createCircleOptions(LatLng latLng, int color) {
        return new CircleOptions()
                .center(latLng)
                .radius(DEFAULT_CIRCLE_SIZE)
                .strokeColor(color)
                .fillColor(color);
    }

    private ArrayList<LatLng> getRoutePoints(List<Location> locations) {
        ArrayList<LatLng> routePoints = new ArrayList<>();
        for (Location location: locations) {
            routePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return routePoints;
    }

    private void displayDefaultMap() {
        try {
            if (hasLocationPermission()) {
                FusedLocationProviderClient locationProviderClient =
                        LocationServices.getFusedLocationProviderClient(mContext);
                Activity activity = getActivity();
                if (activity != null) {
                    locationProviderClient.getLastLocation().addOnSuccessListener(
                            activity,
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng current = new LatLng(location.getLatitude(),
                                                location.getLongitude());
                                        setCurrentLocation(current);
                                    }
                                }
                            }
                    );
                }
            }
        } catch (SecurityException e) {
            Toast.makeText(getActivity(),
                    "Map cannot be displayed without granting location permissions in the settings",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setStartingLocation(LatLng start) {
        removeMarker(mStartingLocation);
        mStartingLocation = mMap.addCircle(createCircleOptions(start, START_MARK_COLOR));

    }

    private void setCurrentLocation(LatLng current) {
        removeMarker(mCurrentLocation);
        mCurrentLocation = mMap.addCircle(createCircleOptions(current, CURRENT_MARK_COLOR));
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        current, DEFAULT_ZOOM));
    }

    private void removeMarker(Circle mark) {
        if (mark != null) {
            mark.remove();
        }
    }

    private void displayToast(boolean locationPermission, boolean sensorRequired) {
        if (!locationPermission) {
            Toast.makeText(getActivity(),
                    "Grant location permissions in the settings",
                    Toast.LENGTH_LONG).show();
        } else if (!sensorRequired) {
            Toast.makeText(getActivity(),
                    "Device does not contain the required sensor for recording workouts",
                    Toast.LENGTH_LONG).show();
        }
    }

}
