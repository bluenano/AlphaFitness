package com.seanschlaefli.nanofitness;

import com.seanschlaefli.nanofitness.util.TimeFormatter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TImeFormatterTest {

    private static final int SIZE = 2;
    private static final int INT_MIN = 1;
    private static final int INT_MAX = 10;
    private static final long LONG_MIN = 1;
    private static final long LONG_MAX = 10;
    private static final float FLOAT_MIN = 1.0f;
    private static final float FLOAT_MAX = 10.0f;

    private static final int SECONDS_PER_DAY = 86_400;
    private static final int SECONDS_PER_HOUR = 3_600;
    private static final int SECONDS_PER_MINUTE = 60;

    @Test
    public void createTimeString() {
        String oneDay = TimeFormatter.createProfileTimeString(SECONDS_PER_DAY);
        String oneHour = TimeFormatter.createProfileTimeString(SECONDS_PER_HOUR);
        String oneMinute = TimeFormatter.createProfileTimeString(SECONDS_PER_MINUTE);
        String oneDayOneHourOneMinute = TimeFormatter.createProfileTimeString(
                SECONDS_PER_DAY + SECONDS_PER_HOUR + SECONDS_PER_MINUTE
        );
        assertEquals(oneDay, "1 day");
        assertEquals(oneHour, "1 hr");
        assertEquals(oneMinute, "1 min");
        assertEquals(oneDayOneHourOneMinute, "1 day 1 hr 1 min");
    }

    @Test
    public void getNumDays() {
        int numDays = TimeFormatter.getNumDays(SECONDS_PER_DAY);
        assertEquals(numDays, 1);
    }

    @Test
    public void getNumHours() {
        int numHours = TimeFormatter.getNumHours(SECONDS_PER_HOUR);
        assertEquals(numHours, 1);
    }

    @Test
    public void getNumMinutes() {
        int numMinutes = TimeFormatter.getNumMinutes(SECONDS_PER_MINUTE);
        assertEquals(numMinutes, 1);
    }
}