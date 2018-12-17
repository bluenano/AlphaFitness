package com.seanschlaefli.alphafitness.database;

public class AlphaFitnessDbSchema {

    public static final String NAME = "AlphaFitness";

    public static final class WorkoutTable {
        public static final String NAME = "workout_table";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String START_TIME = "start_time";
            public static final String END_TIME = "end_time";
            public static final String TOTAL_TIME = "total_time";
            public static final String STEP_COUNT = "step_count";
        }
    }


    public static final class LocationTable {
        public static final String NAME = "location_table";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String WORKOUT_UUID = "workout_uuid";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String RECORD_TIME = "record_time";
            public static final String LOCATION_PROVIDER = "provider";
        }
    }

}
