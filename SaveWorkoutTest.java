package com.seanschlaefli.nanofitness;


import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

//@RunWith(AndroidJUnit4.class)
@SmallTest
public class SaveWorkoutTest {

    private Workout mWorkout;
    private Context mInstrumentationContext;

    @Before
    public void setup() {
        mWorkout = new Workout();
        mInstrumentationContext = InstrumentationRegistry.getContext();
    }

    @Test
    public void testSaveWorkout() {
        /*
        SaveWorkout thread = new SaveWorkout(mInstrumentationContext, mWorkout);
        thread.start();
        try {
            thread.wait();
        } catch (InterruptedException e) {
            fail("Exception in SaveWorkout thread");
        }
        */
    }
}
