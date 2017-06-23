package com.sanderp.bartrider;

// http://www.vogella.com/tutorials/AndroidTesting/article.html#provider_testing
// https://stackoverflow.com/questions/9804917/android-testing-start-with-clean-database-for-every-test

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.database.BartRiderDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@RunWith(AndroidJUnit4.class)
public class BartRiderDbHelperTest {

    private BartRiderDbHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(BartRiderContract.DB_NAME);
        dbHelper = BartRiderDbHelper.getInstance(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        dbHelper.close();
    }

    @Test
    public void shouldAddExpenseType() throws Exception {
        ContentValues values = new ContentValues();
        values.put(BartRiderContract.Favorites.Column.ORIG_ABBR, "CAST");
        values.put(BartRiderContract.Favorites.Column.ORIG_FULL, "Castro Valley");
        values.put(BartRiderContract.Favorites.Column.DEST_ABBR, "MONT");
        values.put(BartRiderContract.Favorites.Column.DEST_FULL, "Montgomery St.");
        getTargetContext().getContentResolver().insert(BartRiderContract.Favorites.CONTENT_URI, values);
    }
}
