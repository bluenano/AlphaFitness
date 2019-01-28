package com.seanschlaefli.nanofitness;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.seanschlaefli.nanofitness.database.AlphaFitnessDbSchema;

import java.util.HashMap;

public class FitnessContentProvider extends ContentProvider {

    public static final String TAG = FitnessContentProvider.class.getSimpleName();

    public static final String PROVIDER = "com.seanschlaefli.alphafitness";

    public static final String URL_WORKOUT = "content://" + PROVIDER + "/workout";
    public static final String URL_LOCATION = "content://" + PROVIDER + "/location";

    public static final Uri URI_WORKOUT = Uri.parse(URL_WORKOUT);
    public static final Uri URI_LOCATION = Uri.parse(URL_LOCATION);

    public static final String _ID = "_id";
    public static final String UUID = "uuid";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String STEP_COUNT = "step_count";
    public static final String TOTAL_TIME = "total_time";
    public static final String WORKOUT_UUID = "workout_uuid";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String RECORD_TIME = "record_time";
    public static final String LOCATION_PROVIDER = "provider";

    private Context mContext;

    public static final int WORKOUT = 1;
    public static final int WORKOUT_ID = 2;
    public static final int LOCATION = 3;
    public static final int LOCATION_ID = 4;

    private static HashMap<String, String> WORKOUT_PROJECTION_MAP;
    private static HashMap<String, String> LOCATION_PROJECTION_MAP;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "workout", WORKOUT);
        uriMatcher.addURI(PROVIDER, "workout/#", WORKOUT_ID);
        uriMatcher.addURI(PROVIDER, "location", LOCATION);
        uriMatcher.addURI(PROVIDER, "location/#", LOCATION_ID);
    }

    private SQLiteDatabase mDb;

    public FitnessContentProvider() {}

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count = 0;
        String selStr = getSelStr(uri, selection);
        switch (uriMatcher.match(uri)) {
            case WORKOUT:
                count = mDb.delete(AlphaFitnessDbSchema.WorkoutTable.NAME,
                        selStr,
                        selectionArgs);
                break;
            case LOCATION:
                count = mDb.delete(AlphaFitnessDbSchema.LocationTable.NAME,
                        selStr,
                        selectionArgs);
                break;
            default:
                throw new SQLException("Failed to query a table");
        }
        notifyChange(uri);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case WORKOUT:
                return "vnd.android.cursor.dir/vnd.seanschlaefli.workout";
            case LOCATION:
                return "vnd.android.cursor.dir/vnd.seanschlaefli.location";
            case WORKOUT_ID:
                return "vnd.android.cursor.item/vnd.seanschlaefli.workout";
            case LOCATION_ID:
                return "vnd.android.cursor.item/vnd.seanschlaefli.location";
            default:
                return "";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {
            case WORKOUT:
                _uri = insertRow(AlphaFitnessDbSchema.WorkoutTable.NAME, values, URI_WORKOUT, _uri);
                break;
            case LOCATION:
                _uri = insertRow(AlphaFitnessDbSchema.LocationTable.NAME, values, URI_LOCATION, _uri);
                break;
            default:
                throw new SQLException("Failed to insert new row into " + uri);
        }
        return _uri;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        if (mContext == null) {
            return false;
        }

        AlphaFitnessDatabaseHelper database = new AlphaFitnessDatabaseHelper(mContext);
        mDb = database.getWritableDatabase();
        if (mDb == null) {
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        switch (uriMatcher.match(uri)) {
            case WORKOUT:
                c = queryTable(AlphaFitnessDbSchema.WorkoutTable.NAME, uri, projection,
                        selection, selectionArgs, sortOrder);
                break;
            case LOCATION:
                c = queryTable(AlphaFitnessDbSchema.LocationTable.NAME, uri, projection,
                        selection, selectionArgs, sortOrder);
                break;
            default:
                throw new SQLException("Failed to query a table");
        }
        c.setNotificationUri(mContext.getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        String selStr = getSelStr(uri, selection);
        switch (uriMatcher.match(uri)) {
            case WORKOUT:
                count = mDb.update(AlphaFitnessDbSchema.WorkoutTable.NAME,
                        values,
                        selStr,
                        selectionArgs);
                break;
            case LOCATION:
                count = mDb.update(AlphaFitnessDbSchema.LocationTable.NAME,
                        values,
                        selStr,
                        selectionArgs);
                break;
            default:
                throw new SQLException("Failed to query a table");
        }
        notifyChange(uri);
        return count;
    }


    private void notifyChange(Uri uri) {
        ContentResolver resolver = mContext.getContentResolver();
        if (resolver != null) {
            resolver.notifyChange(uri, null);
        }
    }

    private int getMatchedID(Uri uri) {
        int matchedID = uriMatcher.match(uri);
        if (! (matchedID == WORKOUT || matchedID == WORKOUT_ID
                || matchedID == LOCATION || matchedID == LOCATION_ID) ) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        return matchedID;
    }

    private String getIDString(Uri uri) {
        return (_ID + " = " + uri.getPathSegments().get(1));
    }

    private String getSelectionWithID(Uri uri, String selection) {
        String sel_str = getIDString(uri);
        if (!TextUtils.isEmpty(selection)) {
            sel_str += " AND (" + selection + ")";
        }
        return sel_str;
    }

    private Uri insertRow(String table, ContentValues values, Uri uri, Uri _uri) {
        long row = mDb.insert(table, "", values);
        if (row > 0) {
            _uri = ContentUris.withAppendedId(uri, row);
            notifyChange(_uri);
            return _uri;
        }
        throw new SQLException("Failed to insert a row");
    }

    private String getSelStr(Uri uri, String selection) {
        int matchedID = getMatchedID(uri);
        if (matchedID == WORKOUT_ID ||
                matchedID == LOCATION_ID) {
             return getSelectionWithID(uri, selection);
        } else {
            return selection;
        }
    }
    private Cursor queryTable(String table, Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(table);
        setProjectionOrAppendID(queryBuilder, uri);
        sortOrder = getSortOrder(sortOrder, table);
        return queryBuilder.query(mDb, projection, selection,
                selectionArgs, null, null, sortOrder);
    }


    private void setProjectionOrAppendID(SQLiteQueryBuilder queryBuilder, Uri uri) {
        switch (getMatchedID(uri)) {
            case WORKOUT:
                queryBuilder.setProjectionMap(WORKOUT_PROJECTION_MAP);
                break;
            case LOCATION:
                queryBuilder.setProjectionMap(LOCATION_PROJECTION_MAP);
                break;
            default:
                queryBuilder.appendWhere( getIDString(uri) );
        }
    }


    private String getSortOrder(String sortOrder, String table) {
        if (sortOrder == null || sortOrder == "") {
            switch (table) {
                case AlphaFitnessDbSchema.WorkoutTable.NAME:
                    sortOrder = AlphaFitnessDbSchema.WorkoutTable.Cols.START_TIME;
                    break;
                case AlphaFitnessDbSchema.LocationTable.NAME:
                    sortOrder = AlphaFitnessDbSchema.LocationTable.Cols.RECORD_TIME;
                    break;
            }
        }
        return sortOrder;
    }

    private static class AlphaFitnessDatabaseHelper extends SQLiteOpenHelper {

        public static final String TAG = AlphaFitnessDatabaseHelper.class.getSimpleName();

        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_WORKOUT_TABLE =
                "CREATE TABLE " + AlphaFitnessDbSchema.WorkoutTable.NAME +
                        " (" + AlphaFitnessDbSchema.WorkoutTable.Cols.ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlphaFitnessDbSchema.WorkoutTable.Cols.UUID + ", " +
                        AlphaFitnessDbSchema.WorkoutTable.Cols.START_TIME + ", " +
                        AlphaFitnessDbSchema.WorkoutTable.Cols.END_TIME + ", " +
                        AlphaFitnessDbSchema.WorkoutTable.Cols.TOTAL_TIME + ", " +
                        AlphaFitnessDbSchema.WorkoutTable.Cols.STEP_COUNT + ");";

        private static final String CREATE_LOCATION_TABLE =
                "CREATE TABLE " + AlphaFitnessDbSchema.LocationTable.NAME +
                        " (" + AlphaFitnessDbSchema.LocationTable.Cols.ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlphaFitnessDbSchema.LocationTable.Cols.WORKOUT_UUID + ", " +
                        AlphaFitnessDbSchema.LocationTable.Cols.LATITUDE + ", " +
                        AlphaFitnessDbSchema.LocationTable.Cols.LONGITUDE + ", " +
                        AlphaFitnessDbSchema.LocationTable.Cols.RECORD_TIME + ", " +
                        AlphaFitnessDbSchema.LocationTable.Cols.LOCATION_PROVIDER + ");";

        public AlphaFitnessDatabaseHelper(Context context) {
            super(context, AlphaFitnessDbSchema.NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_WORKOUT_TABLE);
            db.execSQL(CREATE_LOCATION_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AlphaFitnessDbSchema.WorkoutTable.NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AlphaFitnessDbSchema.LocationTable.NAME);
            onCreate(db);
        }

    }
}
