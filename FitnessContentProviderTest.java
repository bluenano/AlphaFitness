package com.seanschlaefli.nanofitness;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.seanschlaefli.nanofitness.test.MockModels;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class FitnessContentProviderTest extends ProviderTestCase2 {

    private ContentResolver mResolver;
    private Workout mWorkout;

    /**
     * Constructor.
     *
     * @param providerClass     The class name of the provider under test
     * @param providerAuthority The provider's authority string
     */
    public FitnessContentProviderTest(Class providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
        setContext(ApplicationProvider.getApplicationContext());
        mResolver = mContext.getContentResolver();
        mWorkout = MockModels.getMockWorkout();
    }

    @Test
    public void delete() {

    }

    @Test
    public void getType() {

    }

    @Test
    public void insert() {
        Cursor c = null;
        // test inserting Workout table
        ContentValues workout = getWorkoutContentValues();
        mResolver.insert(FitnessContentProvider.URI_WORKOUT, workout);
        c = mResolver.query(FitnessContentProvider.URI_WORKOUT,
                null,
                null,
                null,
                null);
        assertNotNull(c);
        assertEquals(c.getCount(), 1);
        if (!c.moveToFirst()) {
            fail();
        }
        assertEquals(c.getString(c.getColumnIndex(
                FitnessContentProvider.UUID)),
                MockModels.expectedUUID);
        assertEquals(c.getLong(c.getColumnIndex(
                FitnessContentProvider.START_TIME)),
                MockModels.expectedStart);
        assertEquals(c.getLong(c.getColumnIndex(
                FitnessContentProvider.END_TIME)),
                MockModels.expectedEnd);
        assertEquals(c.getInt(c.getColumnIndex(
                FitnessContentProvider.TOTAL_TIME)),
                MockModels.expectedSeconds);
        assertEquals(c.getInt(c.getColumnIndex(
                FitnessContentProvider.STEP_COUNT)),
                MockModels.expectedStepCount);

        // test inserting Location table
        for (int i = 0; i < mWorkout.getLocations().size(); i++) {
            ContentValues location = getLocationContentValues(
                    mWorkout.getLocations().get(i),
                    mWorkout.getIdString(),
                    mWorkout.getLocationRecordTimes().get(i)
            );
            mResolver.insert(FitnessContentProvider.URI_LOCATION, location);
        }
        c = mResolver.query(FitnessContentProvider.URI_LOCATION,
                null,
                null,
                null,
                null);
        assertNotNull(c);
        assertEquals(c.getCount(), 2);
        if (!c.moveToFirst()) {
            fail();
        }
    }

    @Test
    public void query() {
        // test querying Workout table

        // test querying Location table
    }

    @Test
    public void update() {

    }

    private ContentValues getWorkoutContentValues() {
        ContentValues values = new ContentValues();
        values.put(FitnessContentProvider.UUID,
                mWorkout.getIdString());
        values.put(FitnessContentProvider.START_TIME,
                mWorkout.getStartTime());
        values.put(FitnessContentProvider.END_TIME,
                mWorkout.getEndTime());
        values.put(FitnessContentProvider.STEP_COUNT,
                mWorkout.getCurrentStepCount());
        values.put(FitnessContentProvider.TOTAL_TIME,
                mWorkout.getSeconds());
        return values;
    }

    private ContentValues getLocationContentValues(Location location, String workoutId, long recordTime) {
        ContentValues values = new ContentValues();
        values.put(FitnessContentProvider.WORKOUT_UUID,
                workoutId);
        values.put(FitnessContentProvider.LATITUDE,
                location.getLatitude());
        values.put(FitnessContentProvider.LONGITUDE,
                location.getLongitude());
        values.put(FitnessContentProvider.RECORD_TIME,
                recordTime);
        values.put(FitnessContentProvider.LOCATION_PROVIDER,
                location.getProvider());
        return values;
    }
}