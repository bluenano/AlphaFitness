package com.seanschlaefli.nanofitness;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AlphaFitnessUtilTest {

    private static int[] intArr;
    private static long[] longArr;
    private static float[] floatArr;
    private static List<Integer> intList;
    private static List<Long> longList;
    private static List<Float> floatList;

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

    static {
        int[] iArr = new int[2];
        iArr[0] = INT_MIN;
        iArr[1] = INT_MAX;
        intArr = iArr;
        long[] lArr = new long[2];
        lArr[0] = LONG_MIN;
        lArr[1] = LONG_MAX;
        longArr = lArr;
        float[] fArr = new float[2];
        fArr[0] = FLOAT_MIN;
        fArr[1] = FLOAT_MAX;
        floatArr = fArr;
        List<Integer> iList = new ArrayList<>();
        iList.add(INT_MIN);
        iList.add(INT_MAX);
        intList = iList;
        List<Long> lList = new ArrayList<>();
        lList.add(LONG_MIN);
        lList.add(LONG_MAX);
        longList = lList;
        List<Float> fList = new ArrayList<>();
        fList.add(FLOAT_MIN);
        fList.add(FLOAT_MAX);
        floatList = fList;
    }

    @Test
    public void convertIntList() {
        int[] converted = NanoFitnessUtil.convertIntList(intList);
        assertEquals(converted.length, intArr.length);
        for (int i = 0; i < converted.length; i++) {
            assertEquals(converted[i], intArr[i]);
        }
    }

    @Test
    public void convertLongList() {
        long[] converted = NanoFitnessUtil.convertLongList(longList);
        assertEquals(converted.length, longArr.length);
        for (int i = 0; i < converted.length; i++) {
            assertEquals(converted[i], longArr[i]);
        }
    }

    @Test
    public void convertFloatList() {
        float[] converted = NanoFitnessUtil.convertFloatList(floatList);
        assertEquals(converted.length, floatArr.length);
        for (int i = 0; i < converted.length; i++) {
            assertEquals(converted[i], floatArr[i], 0.001f);
        }
    }

    @Test
    public void convertIntArray() {
        List<Integer> converted = NanoFitnessUtil.convertIntArray(intArr);
        assertEquals(converted.size(), SIZE);
        for (int i = 0; i < converted.size(); i++) {
            assertEquals(converted.get(i), intList.get(i));
        }
    }

    @Test
    public void convertLongArray() {
        List<Long> converted = NanoFitnessUtil.convertLongArray(longArr);
        assertEquals(converted.size(), SIZE);
        for (int i = 0; i < converted.size(); i++) {
            assertEquals(converted.get(i), longList.get(i));
        }
    }

    @Test
    public void convertFloatArray() {
        List<Float> converted = NanoFitnessUtil.convertFloatArray(floatArr);
        assertEquals(converted.size(), SIZE);
        for (int i = 0; i < converted.size(); i++) {
            assertEquals(converted.get(i), floatList.get(i));
        }
    }

    @Test
    public void createTimeString() {
        String oneDay = NanoFitnessUtil.createTimeString(SECONDS_PER_DAY);
        String oneHour = NanoFitnessUtil.createTimeString(SECONDS_PER_HOUR);
        String oneMinute = NanoFitnessUtil.createTimeString(SECONDS_PER_MINUTE);
        String oneDayOneHourOneMinute = NanoFitnessUtil.createTimeString(
                SECONDS_PER_DAY + SECONDS_PER_HOUR + SECONDS_PER_MINUTE
        );
        assertEquals(oneDay, "1 day");
        assertEquals(oneHour, "1 hr");
        assertEquals(oneMinute, "1 min");
        assertEquals(oneDayOneHourOneMinute, "1 day 1 hr 1 min");
    }

    @Test
    public void getNumDays() {
        int numDays = NanoFitnessUtil.getNumDays(SECONDS_PER_DAY);
        assertEquals(numDays, 1);
    }

    @Test
    public void getNumHours() {
        int numHours = NanoFitnessUtil.getNumHours(SECONDS_PER_HOUR);
        assertEquals(numHours, 1);
    }

    @Test
    public void getNumMinutes() {
        int numMinutes = NanoFitnessUtil.getNumMinutes(SECONDS_PER_MINUTE);
        assertEquals(numMinutes, 1);
    }
}