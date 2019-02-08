package com.seanschlaefli.nanofitness.util;

import java.util.Locale;

public class TimeFormatter {

    private static final int SECONDS_IN_DAY = 86400;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;

    public static String createWorkoutTimeString(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds % 3600;
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String createProfileTimeString(int timeInSeconds) {
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

    public static int getNumDays(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_DAY;
    }

    public static int getNumHours(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_HOUR;
    }

    public static int getNumMinutes(int timeInSeconds) {
        return timeInSeconds / SECONDS_IN_MINUTE;
    }

    public static String createTimeStringFromRate(float rate) {
        double decimal = rate - Math.floor(rate);
        int seconds = (int) (decimal * 60);
        return String.format(Locale.US, "%d:%02d", (int) rate, seconds);
    }
}
