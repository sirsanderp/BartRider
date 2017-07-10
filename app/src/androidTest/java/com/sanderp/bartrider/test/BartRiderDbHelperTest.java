package com.sanderp.bartrider.test;

// http://www.vogella.com/tutorials/AndroidTesting/article.html#provider_testing
// https://stackoverflow.com/questions/9804917/android-testing-start-with-clean-database-for-every-test

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.database.BartRiderDbHelper;
import com.sanderp.bartrider.utility.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BartRiderDbHelperTest {

    private static final String[] FAV_PROJ = {
            BartRiderContract.Favorites.Column.ID,
            BartRiderContract.Favorites.Column.ORIG_FULL,
            BartRiderContract.Favorites.Column.ORIG_ABBR,
            BartRiderContract.Favorites.Column.DEST_FULL,
            BartRiderContract.Favorites.Column.DEST_ABBR
    };
    private static final String[] STN_PROJ = {
            BartRiderContract.Stations.Column.ID,
            BartRiderContract.Stations.Column.NAME,
            BartRiderContract.Stations.Column.ABBREVIATION
    };

    private BartRiderDbHelper dbHelper;
    private TestUtils utils = new TestUtils();

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(BartRiderContract.DB_NAME);
        dbHelper = BartRiderDbHelper.getInstance(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        dbHelper.close();
        getTargetContext().deleteDatabase(BartRiderContract.DB_NAME);
        utils.clearSharedPrefs();
    }

    @Test
    public void insertFavorite() throws Exception {
        ContentValues values = new ContentValues();
        values.put(BartRiderContract.Favorites.Column.ORIG_ABBR, "CAST");
        values.put(BartRiderContract.Favorites.Column.ORIG_FULL, "Castro Valley");
        values.put(BartRiderContract.Favorites.Column.DEST_ABBR, "MONT");
        values.put(BartRiderContract.Favorites.Column.DEST_FULL, "Montgomery St.");
        getTargetContext().getContentResolver().insert(BartRiderContract.Favorites.CONTENT_URI, values);

        Cursor c = getTargetContext().getContentResolver()
                .query(Uri.parse(BartRiderContract.Favorites.CONTENT_URI + "/1"), FAV_PROJ, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            assertTrue(c.getCount() == 1);
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.ORIG_ABBR)).equals("CAST"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.ORIG_FULL)).equals("Castro Valley"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.DEST_ABBR)).equals("MONT"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.DEST_FULL)).equals("Montgomery St."));
            c.close();
        }
    }

    @Test
    public void insertRecent() throws Exception {
        ContentValues values = new ContentValues();
        values.put(BartRiderContract.Recents.Column.ORIG_ABBR, "CAST");
        values.put(BartRiderContract.Recents.Column.ORIG_FULL, "Castro Valley");
        values.put(BartRiderContract.Recents.Column.DEST_ABBR, "MONT");
        values.put(BartRiderContract.Recents.Column.DEST_FULL, "Montgomery St.");
        getTargetContext().getContentResolver().insert(BartRiderContract.Recents.CONTENT_URI, values);

        Cursor c = getTargetContext().getContentResolver()
                .query(Uri.parse(BartRiderContract.Recents.CONTENT_URI + "/1"), FAV_PROJ, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            assertTrue(c.getCount() == 1);
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Recents.Column.ORIG_ABBR)).equals("CAST"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Recents.Column.ORIG_FULL)).equals("Castro Valley"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Recents.Column.DEST_ABBR)).equals("MONT"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Recents.Column.DEST_FULL)).equals("Montgomery St."));
            c.close();
        }
    }

    @Test
    public void insertStation() throws Exception {
        ContentValues values = new ContentValues();
        values.put(BartRiderContract.Stations.Column.ID, 1);
        values.put(BartRiderContract.Stations.Column.NAME, "Ashby");
        values.put(BartRiderContract.Stations.Column.ABBREVIATION, "ASHB");
        values.put(BartRiderContract.Stations.Column.LATITUDE, "37.852803");
        values.put(BartRiderContract.Stations.Column.LONGITUDE, "-122.270062");
        values.put(BartRiderContract.Stations.Column.ADDRESS, "3100 Adeline Street");
        values.put(BartRiderContract.Stations.Column.CITY, "Berkeley");
        values.put(BartRiderContract.Stations.Column.COUNTY, "alameda");
        values.put(BartRiderContract.Stations.Column.STATE, "CA");
        values.put(BartRiderContract.Stations.Column.ZIPCODE, "94703");
        getTargetContext().getContentResolver().insert(BartRiderContract.Stations.CONTENT_URI, values);

        Cursor c = getTargetContext().getContentResolver()
                .query(Uri.parse(BartRiderContract.Stations.CONTENT_URI + "/1"), STN_PROJ, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            assertTrue(c.getCount() == 1);
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.NAME)).equals("Ashby"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ABBREVIATION)).equals("ASHB"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.LATITUDE)).equals("37.852803"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.LONGITUDE)).equals("-122.270062"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ADDRESS)).equals("3100 Adeline Street"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.CITY)).equals("Berkeley"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.COUNTY)).equals("alameda"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.STATE)).equals("CA"));
            assertTrue(c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ZIPCODE)).equals("94703"));
            c.close();
        }
    }
}
