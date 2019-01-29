package com.seanschlaefli.nanofitness;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecordWorkoutFragment extends Fragment {

    public static final String TAG = RecordWorkoutFragment.class.getSimpleName();
    public static final String RECORD_KEY = "is_workout_started_key";
    public static final String DISTANCE_KEY = "workout_distance_key";
    public static final String TIME_KEY = "workout_time_key";
    public static final String LOCATIONS_KEY = "locations_key";

    private static final int DEFAULT_ZOOM = 15;
    private static final int DEFAULT_CIRCLE_SIZE = 10;
    private static final int START_MARK_COLOR = Color.BLACK;
    private static final int CURRENT_MARK_COLOR = Color.RED;

    private boolean mLocationPermissionGranted;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private List<Location> mLocations;

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
        void startProfileActivity(boolean isRecording);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workout, container, false);

        getLocationPermission();
        ImageButton profileButton = v.findViewById(R.id.profile_button_id);
        mRecordButton = v.findViewById(R.id.record_button_id);
        mDistance = v.findViewById(R.id.distance_id);
        mDuration = v.findViewById(R.id.duration_id);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.startProfileActivity(mIsRecording);
            }
        });

        float distance = 0.0f;
        int seconds = 0;
        Bundle args = getArguments();
        if (args != null) {
            mIsRecording = args.getBoolean(RECORD_KEY);
            distance = args.getFloat(DISTANCE_KEY);
            seconds = args.getInt(TIME_KEY);
            mLocations = args.getParcelableArrayList(LOCATIONS_KEY);
        } else {
            mIsRecording = false;
            mLocations = new ArrayList<>();
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

        getChildFragmentManager().beginTransaction().replace(R.id.map_id, mMapFragment).commit();
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mIsRecording) {
                    updateMap(mLocations);
                } else {
                    displayDefaultMap();
                }
            }
        });

        return v;
    }


    public static RecordWorkoutFragment newInstance() {
        return new RecordWorkoutFragment();
    }


    private void handleRecordStateChange() {
        if (mIsRecording) {
            mIsRecording = false;
            mCallback.workoutEnded(Calendar.getInstance().getTimeInMillis());
            mRecordButton.setText(getResources().getString(R.string.start_workout));
        } else {
            resetMap();
            mIsRecording = true;
            mCallback.workoutStarted(Calendar.getInstance().getTimeInMillis());
            mRecordButton.setText(getResources().getString(R.string.stop_workout));
            initializeMap();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnRecordChange) context;
        mContext = context;
    }

    private void getLocationPermission() {
        Context context = getActivity();
        if (context != null) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                mLocationPermissionGranted = false;
                Toast.makeText(context,
                        "Grant Location Permission to Enable Location Service",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            mLocationPermissionGranted = false;
        }
    }

    public void updateDistance(float distance) {
        mDistance.setText(String.format(Locale.US, "%.1f", distance));
    }

    public void updateDuration(int duration) {
        mDuration.setText(createTimeString(duration));
    }

    private void setRecordText() {
        if (mIsRecording) {
            mRecordButton.setText(getResources().getString(R.string.stop_workout));
        } else {
            mRecordButton.setText(getResources().getString(R.string.start_workout));
        }
    }

    public String createTimeString(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds % 3600;
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }


    private void initializeMap() {
        int size = mLocations.size();
        if (mIsRecording && size > 0) {
            updateMap(mLocations);
            showRoute();
        } else {
            displayDefaultMap();
        }

    }

    private void resetMap() {
        mMap.clear();
        mStartingLocation = null;
        mCurrentLocation = null;
        mRoute = null;
        mLocations = new ArrayList<>();
    }

    public void updateMap(List<Location> locations) {
        mLocations = new ArrayList<>(locations);
        int size = mLocations.size();
        if (size > 0) {
            Location start = mLocations.get(0);
            Location end = mLocations.get(size-1);
            setStartingLocation(new LatLng(start.getLatitude(),
                    start.getLongitude()));
            setCurrentLocation(new LatLng(end.getLatitude(),
                    end.getLongitude()));
            showRoute();
        }
    }

    private void showRoute() {
        ArrayList<LatLng> routePoints = getRoutePoints();
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

    private ArrayList<LatLng> getRoutePoints() {
        ArrayList<LatLng> routePoints = new ArrayList<>();
        for (Location location: mLocations) {
            routePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return routePoints;
    }

    private void displayDefaultMap() {
        try {
            if (mLocationPermissionGranted) {
                FusedLocationProviderClient locationProviderClient =
                        LocationServices.getFusedLocationProviderClient(mContext);
                final Task locationResult = locationProviderClient.getLastLocation();
                Activity activity = getActivity();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location lastKnown = (Location) task.getResult();
                            if (lastKnown != null) {
                                LatLng current = new LatLng(lastKnown.getLatitude(),
                                        lastKnown.getLongitude());
                                setCurrentLocation(current);
                            }
                        }
                    }
                });
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

}
