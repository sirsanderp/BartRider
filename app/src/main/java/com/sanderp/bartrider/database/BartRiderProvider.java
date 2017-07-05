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
 * Methods for accessing the SQLite tables on the device.
 */
public class BartRiderProvider extends ContentProvider {
    private static final String TAG = "BartRiderProvider";

    private BartRiderDbHelper mStationDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Favorites.TABLE,
                BartRiderContract.FAVORITE_TABLE);
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Favorites.TABLE + "/#",
                BartRiderContract.FAVORITE_ITEM);
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Recents.TABLE,
                BartRiderContract.RECENTS_TABLE);
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Recents.TABLE + "/#",
                BartRiderContract.RECENTS_ITEM);
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Stations.TABLE,
                BartRiderContract.STATION_TABLE);
        sURIMatcher.addURI(BartRiderContract.AUTHORITY, BartRiderContract.Stations.TABLE + "/#",
                BartRiderContract.STATION_ITEM);
    }

    @Override
    public boolean onCreate() {
        mStationDbHelper = BartRiderDbHelper.getInstance(getContext());
        return mStationDbHelper != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        String defaultSort;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sURIMatcher.match(uri)) {
            case BartRiderContract.FAVORITE_TABLE:
                defaultSort = BartRiderContract.Favorites.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Favorites.TABLE);
                break;
            case BartRiderContract.FAVORITE_ITEM:
                defaultSort = BartRiderContract.Favorites.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Favorites.TABLE);
                qb.appendWhere(BartRiderContract.Favorites.Column.ID + "=" + uri.getLastPathSegment());
                break;
            case BartRiderContract.RECENTS_TABLE:
                defaultSort = BartRiderContract.Recents.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Recents.TABLE);
                break;
            case BartRiderContract.RECENTS_ITEM:
                defaultSort = BartRiderContract.Recents.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Recents.TABLE);
                qb.appendWhere(BartRiderContract.Recents.Column.ID + "=" + uri.getLastPathSegment());
                break;
            case BartRiderContract.STATION_TABLE:
                defaultSort = BartRiderContract.Stations.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Stations.TABLE);
                break;
            case BartRiderContract.STATION_ITEM:
                defaultSort = BartRiderContract.Stations.DEFAULT_SORT;
                qb.setTables(BartRiderContract.Stations.TABLE);
                qb.appendWhere(BartRiderContract.Stations.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder)) ? defaultSort : sortOrder;
        SQLiteDatabase db = mStationDbHelper.getReadableDatabase();

        Log.d(TAG, qb.toString());
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String columnId, table;
        switch (sURIMatcher.match(uri)) {
            case BartRiderContract.FAVORITE_TABLE:
                columnId = BartRiderContract.Favorites.Column.ID;
                table = BartRiderContract.Favorites.TABLE;
                break;
            case BartRiderContract.RECENTS_TABLE:
                columnId = BartRiderContract.Recents.Column.ID;
                table = BartRiderContract.Recents.TABLE;
                break;
            case BartRiderContract.STATION_TABLE:
                columnId = BartRiderContract.Stations.Column.ID;
                table = BartRiderContract.Stations.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mStationDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // If values were inserted successfully
        Uri ret = null;
        if (rowId != -1) {
//            if (values.getAsLong(columnId) != null) {
//                long id = values.getAsLong(columnId);
//                ret = ContentUris.withAppendedId(uri, id);
//            } else {
//                ret = uri;
//            }
            ret = ContentUris.withAppendedId(uri, rowId);
            Log.d(TAG, "inserted uri: " + ret);

            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
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
        long id;
        String table, where;
        switch (sURIMatcher.match(uri)) {
            case BartRiderContract.FAVORITE_TABLE:
                table = BartRiderContract.Favorites.TABLE;
                where = null;
                break;
            case BartRiderContract.FAVORITE_ITEM:
                id = ContentUris.parseId(uri);
                table = BartRiderContract.Favorites.TABLE;
                where = BartRiderContract.Favorites.Column.ID + "=" + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            case BartRiderContract.RECENTS_TABLE:
                table = BartRiderContract.Recents.TABLE;
                where = null;
                break;
            case BartRiderContract.RECENTS_ITEM:
                id = ContentUris.parseId(uri);
                table = BartRiderContract.Recents.TABLE;
                where = BartRiderContract.Recents.Column.ID + "=" + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            case BartRiderContract.STATION_TABLE:
                table = BartRiderContract.Stations.TABLE;
                where = null;
                break;
            case BartRiderContract.STATION_ITEM:
                id = ContentUris.parseId(uri);
                table = BartRiderContract.Stations.TABLE;
                where = BartRiderContract.Stations.Column.ID + "=" + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mStationDbHelper.getWritableDatabase();
        Log.d(TAG, String.format("%s %s", table, where));
        int ret = db.delete(table, where, selectionArgs);

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
