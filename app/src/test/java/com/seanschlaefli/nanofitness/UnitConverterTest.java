package com.seanschlaefli.nanofitness;

import com.seanschlaefli.nanofitness.util.UnitConverter;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnitConverterTest {

    @Test
    public void stepCountToFt() {
        int stepCount = 1000;
        float multiplier = 1.6577f;
        float expected = 1657.7f;
        float result = UnitConverter.stepCountToFt(stepCount, multiplier);
        assertEquals(expected, result, 0.1);
    }

    @Test
    public void ftToMiles() {
        float ft = 6400.0f;
        float expected = 1.2f;
        float result = UnitConverter.ftToMiles(ft);
        assertEquals(expected, result, 0.1);
    }

    @Test
    public void stepCountToMiles() {
        int stepCount = 1000;
        float multiplier = 1.6577f;
        float expected = 0.31f;
        float result = UnitConverter.stepCountToMiles(stepCount, multiplier);
        assertEquals(expected, result, 0.01);
    }

    @Test
    public void msToMinutes() {
        long timeInMs = 120_000;
        float expected = 2.0f;
        float result = UnitConverter.msToMinutes(timeInMs);
        assertEquals(expected, result, 0.1);
    }
}