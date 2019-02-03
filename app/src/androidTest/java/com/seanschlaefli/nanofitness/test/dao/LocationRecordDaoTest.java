package com.seanschlaefli.nanofitness.test.dao;

import android.content.Context;

import com.seanschlaefli.nanofitness.dao.LocationRecordDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.model.LocationRecord;
import com.seanschlaefli.nanofitness.model.Workout;
import com.seanschlaefli.nanofitness.test.TestUtil;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
public class LocationRecordDaoTest {

    private LocationRecordDao locationRecordDao;
    private AppDatabase testDatabase;

    private static final double expectedLatitude = -33.89;
    private static final double expectedLongitude = 151.21;
    private static final long expectedTime = 100;
    private static final String expectedProvider = "";

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        testDatabase = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase.class
        ).build();
        locationRecordDao = testDatabase.locationRecordDao();
    }

    @After
    public void closeDb() {
        testDatabase.close();
    }


    @Test
    public void writeAndReadSingleLocationRecord() throws Exception {
        Workout workout = TestUtil.createWorkout(0);
        int workoutId = (int) testDatabase.workoutDao().insert(workout);
        LocationRecord record = TestUtil.createLocationRecord(workoutId, expectedLatitude,
                expectedLongitude, expectedTime, expectedProvider);
        int id = (int) locationRecordDao.insert(record);
        List<LocationRecord> records = locationRecordDao.getAll();
        assertEquals(records.size(), 1);
        assertEquals(records.get(0).latitude, expectedLatitude, 0.01);
        assertEquals(records.get(0).longitude, expectedLongitude, 0.01);
        assertEquals(records.get(0).recordTime, expectedTime);
        assertEquals(records.get(0).locationProvider, expectedProvider);
        assertEquals(records.get(0).workoutId, workoutId);
        LocationRecord result = locationRecordDao.loadById(id);
        assertEquals(result.latitude, expectedLatitude, 0.01);
        assertEquals(result.longitude, expectedLongitude, 0.01);
        assertEquals(result.recordTime, expectedTime);
        assertEquals(result.locationProvider, expectedProvider);
        assertEquals(result.workoutId, workoutId);
    }

    @Test
    public void deleteSingleLocationRecord() {
        Workout workout = TestUtil.createWorkout(0);
        int workoutId = (int) testDatabase.workoutDao().insert(workout);
        LocationRecord record = TestUtil.createLocationRecord(workoutId, expectedLatitude,
                expectedLongitude, expectedTime, expectedProvider);
        int id = (int) locationRecordDao.insert(record);
        record.id = id;
        locationRecordDao.delete(record);
        List<LocationRecord> records = locationRecordDao.getAll();
        assertEquals(records.size(), 0);
    }

    @Test
    public void updateSingleLocationRecord() {
        Workout workout = TestUtil.createWorkout(0);
        int workoutId = (int) testDatabase.workoutDao().insert(workout);
        LocationRecord record = TestUtil.createLocationRecord(workoutId, expectedLatitude,
                expectedLongitude, expectedTime, expectedProvider);
        int id = (int) locationRecordDao.insert(record);
        record.id = id;
        record.recordTime = expectedTime + 10;
        locationRecordDao.update(record);
        LocationRecord result = locationRecordDao.loadById(record.id);
        assertEquals(result.latitude, expectedLatitude, 0.01);
        assertEquals(result.longitude, expectedLongitude, 0.01);
        assertEquals(result.recordTime, expectedTime + 10);
        assertEquals(result.locationProvider, expectedProvider);
        assertEquals(result.workoutId, workoutId);
    }
}
