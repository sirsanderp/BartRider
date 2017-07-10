package com.sanderp.bartrider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates the device SQLite tables: favorites, stations.
 */
public class BartRiderDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "BartRiderDbHelper";

    private static BartRiderDbHelper mInstance = null;

    private BartRiderDbHelper(Context context) {
        super(context, BartRiderContract.DB_NAME, null, BartRiderContract.DB_VERSION);
    }

    public static BartRiderDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BartRiderDbHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String favoritesTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s text, %s text, %s text, %s text)",
                BartRiderContract.Favorites.TABLE,
                BartRiderContract.Favorites.Column.ID,
                BartRiderContract.Favorites.Column.ORIG_FULL,
                BartRiderContract.Favorites.Column.ORIG_ABBR,
                BartRiderContract.Favorites.Column.DEST_FULL,
                BartRiderContract.Favorites.Column.DEST_ABBR
        );
        Log.d(TAG, "onCreate with SQL: " + favoritesTable);
        db.execSQL(favoritesTable);

        String recentsTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s text, %s text, %s text, %s text)",
                BartRiderContract.Recents.TABLE,
                BartRiderContract.Recents.Column.ID,
                BartRiderContract.Recents.Column.ORIG_FULL,
                BartRiderContract.Recents.Column.ORIG_ABBR,
                BartRiderContract.Recents.Column.DEST_FULL,
                BartRiderContract.Recents.Column.DEST_ABBR
        );
        Log.d(TAG, "onCreate with SQL: " + recentsTable);
        db.execSQL(recentsTable);

        String stationsTable = String.format(
                "CREATE TABLE %s (%s int PRIMARY KEY, %s text, %s text, %s text, %s text, " +
                        "%s text, %s text, %s text, %s text, %s text)",
                BartRiderContract.Stations.TABLE,
                BartRiderContract.Stations.Column.ID,
                BartRiderContract.Stations.Column.NAME,
                BartRiderContract.Stations.Column.ABBREVIATION,
                BartRiderContract.Stations.Column.LATITUDE,
                BartRiderContract.Stations.Column.LONGITUDE,
                BartRiderContract.Stations.Column.ADDRESS,
                BartRiderContract.Stations.Column.CITY,
                BartRiderContract.Stations.Column.COUNTY,
                BartRiderContract.Stations.Column.STATE,
                BartRiderContract.Stations.Column.ZIPCODE
        );
        Log.d(TAG, "onCreate with SQL: " + stationsTable);
        db.execSQL(stationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // When BartRiderContract.DB_VERSION is increased.
        String dropFavorites = "DROP TABLE IF EXISTS " + BartRiderContract.Favorites.TABLE;
        db.execSQL(dropFavorites);
        String dropRecents = "DROP TABLE IF EXISTS " + BartRiderContract.Recents.TABLE;
        db.execSQL(dropRecents);
        String dropStations = "DROP TABLE IF EXISTS " + BartRiderContract.Stations.TABLE;
        db.execSQL(dropStations);

        onCreate(db);
    }
}
