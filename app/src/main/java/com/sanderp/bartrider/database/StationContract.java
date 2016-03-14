package com.sanderp.bartrider.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sander on 3/13/2016.
 */
public class StationContract {

    public static final String DB_NAME = "bart.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "stations";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String ABBREVATION = "abbr";
        public static final String LATITUDE = "gtfs_latitude";
        public static final String LONGITDUE = "gtfs_longitude";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String COUNTY = "county";
        public static final String STATE = "state";
        public static final String ZIPCODE = "zipcode";
    }

    public static final String AUTHORITY = "com.sanderp.bartrider.database.StationProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);

    public static final int STATION_ITEM = 1;
    public static final int STATION_DIR = 2;

    public static final String DEFAULT_SORT = Column.NAME + " DESC";
}
