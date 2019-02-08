package com.seanschlaefli.nanofitness.viewmodel;

import android.app.Application;
import android.location.Location;

import com.seanschlaefli.nanofitness.model.LocationRecord;
import com.seanschlaefli.nanofitness.repo.LocalLocationRecordRepository;
import com.seanschlaefli.nanofitness.repo.LocationRecordRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LocationRecordViewModel extends AndroidViewModel {

    public LocationRecordRepository mRepository;
    public LiveData<List<LocationRecord>> mAllLocations;

    public LocationRecordViewModel(@NonNull Application application, Integer workoutId) {
        super(application);
        mRepository = new LocalLocationRecordRepository(application, workoutId);
        mAllLocations = mRepository.loadAllByWorkout(workoutId);
    }

    public LiveData<List<LocationRecord>> getAllLocations() {
        return mAllLocations;
    }

    public void setAllLocations(int workoutId) {
        mAllLocations = mRepository.loadAllByWorkout(workoutId);
    }

    public List<Location> getLocationsFromRecords() {
        List<LocationRecord> records = mAllLocations.getValue();
        List<Location> locations = new ArrayList<>();
        if (records != null) {
            for (LocationRecord record: records) {
                locations.add(record.getLocation());
            }
        }
        return locations;
    }

}
