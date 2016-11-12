package com.sanderp.bartrider.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Sander Peerna on 3/13/2016.
 */
public class StationProvider extends ContentProvider {
    private static final String TAG = "StationProvider";
    private StationDbHelper mStationDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(StationContract.AUTHORITY, StationContract.TABLE,
                StationContract.STATION_DIR);
        sURIMatcher.addURI(StationContract.AUTHORITY, StationContract.TABLE + "/#",
                StationContract.STATION_ITEM);
    }

    @Override
    public boolean onCreate() {
        mStationDbHelper = new StationDbHelper(getContext());
        return (mStationDbHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(StationContract.TABLE);

        switch (sURIMatcher.match(uri)) {
            // Get the whole table
            case StationContract.STATION_DIR:
                break;
            // Get the specified row
            case StationContract.STATION_ITEM:
                qb.appendWhere(StationContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        String orderBy = (TextUtils.isEmpty(sortOrder)) ? StationContract.DEFAULT_SORT : sortOrder;

        SQLiteDatabase db = mStationDbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

//        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        // Assert correct uri
        if (sURIMatcher.match(uri) != StationContract.STATION_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mStationDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(StationContract.TABLE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);

        // If values were inserted successfully
        if (rowId != -1) {
            long id = values.getAsLong(StationContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            // Notify that data for this uri has changed
//            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case StationContract.STATION_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case StationContract.STATION_ITEM:
                long id = ContentUris.parseId(uri);
                where = StationContract.Column.ID + "=" + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mStationDbHelper.getWritableDatabase();
        int ret = db.delete(StationContract.TABLE, where, selectionArgs);

        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
