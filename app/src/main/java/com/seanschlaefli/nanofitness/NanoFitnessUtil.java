package com.seanschlaefli.nanofitness;

import java.util.ArrayList;
import java.util.List;

public class NanoFitnessUtil {

    private static final int SECONDS_IN_DAY = 86400;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;

    public static int[] convertIntList(List<Integer> intList) {
        if (intList == null) {
            return new int[0];
        }
        int[] arr = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            arr[i] = intList.get(i);
        }
        return arr;
    }

    public static long[] convertLongList(List<Long> longList) {
        if (longList == null) {
            return new long[0];
        }
        long[] arr = new long[longList.size()];
        for (int i = 0; i < longList.size(); i++) {
            arr[i] = longList.get(i);
        }
        return arr;
    }

    public static float[] convertFloatList(List<Float> floatList) {
        if (floatList == null) {
            return new float[0];
        }
        float[] arr = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            arr[i] = floatList.get(i);
        }
        return arr;
    }

    public static ArrayList<Integer> convertIntArray(int[] arr) {
        ArrayList<Integer> retList = new ArrayList<>(arr.length);
        for (int i: arr) {
            retList.add(i);
        }
        return retList;
    }

    public static ArrayList<Long> convertLongArray(long[] arr) {
        ArrayList<Long> retList = new ArrayList<>(arr.length);
        for (long l: arr) {
            retList.add(l);
        }
        return retList;
    }

    public static ArrayList<Float> convertFloatArray(float[] arr) {
        ArrayList<Float> retList = new ArrayList<>(arr.length);
        for (float f: arr) {
            retList.add(f);
        }
        return retList;
    }


    public static String createTimeString(int timeInSeconds) {
        String daysStr = "";
        String hoursStr = "";
        String minutesStr = "";
        String secondsStr = "";
        int days = getNumDays(timeInSeconds);
        if (days > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_DAY;
            daysStr = Integer.toString(days) + " day";
        }
        int hours = getNumHours(timeInSeconds);
        if (hours > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_HOUR;
            hoursStr = Integer.toString(hours) + " hr";
        }
        int minutes = getNumMinutes(timeInSeconds);
        if (minutes > 0) {
            timeInSeconds = timeInSeconds % SECONDS_IN_MINUTE;
            minutesStr = Integer.toString(minutes) + " min";
        }
        if (timeInSeconds > 0) {
            secondsStr = " " + Integer.toString(timeInSeconds) + " sec";
        }
        String result = daysStr + " " + hoursStr + " " +
                minutesStr + " " + secondsStr;
        return result.trim();
    }

    static int getNumDays(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_DAY;
    }

    static int getNumHours(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_HOUR;
    }

    static int getNumMinutes(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_MINUTE;
    }
}
