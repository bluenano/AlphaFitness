package com.seanschlaefli.nanofitness.test.dao;

import android.content.Context;

import com.seanschlaefli.nanofitness.database.dao.WorkoutDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.database.entity.Workout;
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
public class WorkoutDaoTest {

    private WorkoutDao workoutDao;
    private AppDatabase testDatabase;

    public static long expectedStartTime = 10;
    public static long expectedEndTime = 100;
    public static int expectedSteps = 1000;
    public static int expectedSeconds = 90;
    public static float expectedMaxRate = 5.0f;
    public static float expectedMinRate = 1.0f;
    public static float expectedAvgRate = 2.5f;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        testDatabase = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase.class
        ).build();
        workoutDao = testDatabase.workoutDao();
    }

    @After
    public void closeDb() {
        testDatabase.close();
    }

    @Test
    public void writeAndReadSingleWorkout() {
        Workout workout = TestUtil.createWorkout(expectedStartTime, expectedEndTime,
                expectedSteps, expectedSeconds, expectedMaxRate, expectedMinRate,
                expectedAvgRate);
        int id = (int) workoutDao.insert(workout);
        List<Workout> workouts = workoutDao.getAll();
        assertEquals(workouts.size(), 1);
        assertEquals(workouts.get(0).mId, id);
        assertEquals(workouts.get(0).mStartTime, expectedStartTime);
        assertEquals(workouts.get(0).mEndTime, expectedEndTime);
        assertEquals(workouts.get(0).mStepCount, expectedSteps);
        assertEquals(workouts.get(0).mNumSeconds, expectedSeconds);
        assertEquals(workouts.get(0).mMaxRate, expectedMaxRate, 0.1f);
        assertEquals(workouts.get(0).mMinRate, expectedMinRate, 0.1f);
        assertEquals(workouts.get(0).mAvgRate, expectedAvgRate, 0.1f);
        Workout result = workoutDao.loadById(id);
        assertEquals(result.mId, id);
        assertEquals(result.mStartTime, expectedStartTime);
        assertEquals(result.mEndTime, expectedEndTime);
        assertEquals(result.mStepCount, expectedSteps);
        assertEquals(result.mNumSeconds, expectedSeconds);
        assertEquals(result.mMaxRate, expectedMaxRate, 0.1f);
        assertEquals(result.mMinRate, expectedMinRate, 0.1f);
        assertEquals(result.mAvgRate, expectedAvgRate, 0.1f);
    }

    @Test
    public void deleteSingleWorkout() {
        Workout workout = TestUtil.createWorkout(expectedStartTime);
        int id = (int) workoutDao.insert(workout);
        workout.mId = id;
        workoutDao.delete(workout);
        List<Workout> workouts = workoutDao.getAll();
        assertEquals(workouts.size(), 0);
    }

    @Test
    public void updateSingleWorkout() {
        Workout workout = TestUtil.createWorkout(expectedStartTime);
        int id = (int) workoutDao.insert(workout);
        workout.mId = id;
        workout.mStartTime = expectedStartTime + expectedEndTime;
        workoutDao.update(workout);
        Workout result = workoutDao.loadById(workout.mId);
        assertEquals(result.mStartTime, expectedStartTime + expectedEndTime);
    }
}
