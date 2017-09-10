package com.amul.dc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelperClass extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "amul_database";
    private static final int DATABASE_VERSION = 1;
    private final String TEXT_TYPE = " TEXT";
    private final String COMMA_SEP = ", ";
    private static DBOpenHelperClass instance;
    public Context context;

    // Table
    public final String TABLE_STORE_DETAILS = "STORE_DETAILS";

    // Column
    public final String COLUMN_UNIQUE_ID = "UNIQUE_ID";  //PRIMARY KEY
    public final String COLUMN_STORE_NAME = "STORE_NAME ";
    public final String COLUMN_STORE_LOCATION = "STORE_LOCATION";
    public final String COLUMN_SCAN_DATE_TIME = "SCAN_DATE_TIME";
    public final String COLUMN_GPS_COORDINATE = "GPS_COORDINATE";
    public final String COLUMN_CITY_ID = "CITY_ID";
    public final String COLUMN_ROUTE_ID = "ROUTE_ID";
    public final String COLUMN_LATITUDE = "LATITUDE";
    public final String COLUMN_LONGITUDE = "LONGITUDE";

    public final String COLUMN_IMAGE_1 = "IMAGE_1";
    public final String COLUMN_IMAGE_2 = "IMAGE_2";
    public final String COLUMN_STATUS = "STATUS";

    public final String COLUMN_ADDITIONAL_1 = "ADDITIONAL_1";
    public final String COLUMN_ADDITIONAL_2 = "ADDITIONAL_2";
    public final String COLUMN_ADDITIONAL_3 = "ADDITIONAL_3";


    private final String CREATE_TABLE_STORE_DETAILS = "CREATE TABLE " + TABLE_STORE_DETAILS + " ( "
            + COLUMN_UNIQUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_STORE_NAME + TEXT_TYPE + COMMA_SEP
            + COLUMN_STORE_LOCATION + TEXT_TYPE + COMMA_SEP
            + COLUMN_SCAN_DATE_TIME + TEXT_TYPE + COMMA_SEP
            + COLUMN_GPS_COORDINATE + TEXT_TYPE + COMMA_SEP

            + COLUMN_CITY_ID + TEXT_TYPE + COMMA_SEP
            + COLUMN_ROUTE_ID + TEXT_TYPE + COMMA_SEP
            + COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP
            + COLUMN_LONGITUDE + TEXT_TYPE + COMMA_SEP

            + COLUMN_IMAGE_1 + " BLOB" + COMMA_SEP
            + COLUMN_IMAGE_2 + " BLOB" + COMMA_SEP
            + COLUMN_STATUS + TEXT_TYPE + COMMA_SEP
            + COLUMN_ADDITIONAL_1 + TEXT_TYPE + COMMA_SEP
            + COLUMN_ADDITIONAL_2 + TEXT_TYPE + COMMA_SEP
            + COLUMN_ADDITIONAL_3 + TEXT_TYPE + " );";

    public static DBOpenHelperClass getSharedObject(Context context) {
        if (instance == null) {
            instance = new DBOpenHelperClass(context);
        }
        instance.context = context;
        return instance;
    }

    public DBOpenHelperClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase sqdb = super.getWritableDatabase();
        return sqdb;
    }

    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_STORE_DETAILS);
        } catch (Exception ex) {
            Log.d("DBException", ex.getMessage());
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
