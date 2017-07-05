package com.sanderp.bartrider.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants for accessing SQLite tables on the device.
 */
public class BartRiderContract {
    public static final String AUTHORITY = "com.sanderp.bartrider.database.provider";
    public static final String DB_NAME = "bart_rider.db";
    public static final int DB_VERSION = 1;

    public static final int FAVORITE_ITEM = 1;
    public static final int FAVORITE_TABLE = 2;
    public static final int RECENTS_ITEM = 5;
    public static final int RECENTS_TABLE = 6;
    public static final int STATION_ITEM = 3;
    public static final int STATION_TABLE = 4;

    public static class Favorites {
        public static final String TABLE = "favorites";

        public static class Column {
            public static final String ID = BaseColumns._ID;
            public static final String ORIG_FULL = "orig_full";
            public static final String ORIG_ABBR = "orig_abbr";
            public static final String DEST_FULL = "dest_full";
            public static final String DEST_ABBR = "dest_abbr";
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
        public static final String DEFAULT_SORT = Column.ID + " ASC";
    }

    public static class Recents {
        public static final String TABLE = "recents";

        public static class Column {
            public static final String ID = BaseColumns._ID;
            public static final String ORIG_FULL = "orig_full";
            public static final String ORIG_ABBR = "orig_abbr";
            public static final String DEST_FULL = "dest_full";
            public static final String DEST_ABBR = "dest_abbr";
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
        public static final String DEFAULT_SORT = Column.ID + " DESC";
    }

    public static class Stations {
        public static final String TABLE = "stations";

        public static class Column {
            public static final String ID = BaseColumns._ID;
            public static final String NAME = "name";
            public static final String ABBREVIATION = "abbr";
            public static final String LATITUDE = "gtfs_latitude";
            public static final String LONGITUDE = "gtfs_longitude";
            public static final String ADDRESS = "address";
            public static final String CITY = "city";
            public static final String COUNTY = "county";
            public static final String STATE = "state";
            public static final String ZIPCODE = "zipcode";
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
        public static final String DEFAULT_SORT = Column.NAME + " ASC";
    }
}
