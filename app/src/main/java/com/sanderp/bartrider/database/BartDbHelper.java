package com.sanderp.bartrider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sander on 3/13/2016.
 */
public class BartDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "BartDbHelper";

    public BartDbHelper(Context context) {
        super(context, StationContract.DB_NAME, null, StationContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE %s (%s int PRIMARY KEY, %s text, %s text, %s text, %s text, " +
                        "%s text, %s text, %s text, %s text, %s text)",
                StationContract.TABLE,
                StationContract.Column.ID,
                StationContract.Column.NAME,
                StationContract.Column.ABBREVATION,
                StationContract.Column.LATITUDE,
                StationContract.Column.LONGITDUE,
                StationContract.Column.ADDRESS,
                StationContract.Column.CITY,
                StationContract.Column.COUNTY,
                StationContract.Column.STATE,
                StationContract.Column.ZIPCODE
        );

        Log.d(TAG, "onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + StationContract.TABLE;

        db.execSQL(sql);
        onCreate(db);
    }
}
