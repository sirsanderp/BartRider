package com.sanderp.bartrider.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.matcher.CursorMatchers;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.database.BartRiderContract;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class TestUtils {
    public void clearSharedPrefs(boolean firstRun) {
        SharedPreferences.Editor sharedPrefs = InstrumentationRegistry.getTargetContext().getSharedPreferences(PrefContract.PREFS_NAME, Context.MODE_PRIVATE).edit();
        sharedPrefs.clear();
        sharedPrefs.putBoolean(PrefContract.FIRST_RUN, firstRun);
        sharedPrefs.commit();
    }

    public void dropTable(Uri table) {
        getTargetContext().getContentResolver().delete(table, null, null);
    }

    public void checkListViewItem(int id, int position, String text, boolean matches) {
        DataInteraction data = onData(instanceOf(Cursor.class)).inAdapterView(allOf(withId(id), isDisplayed())).atPosition(position);
        if (matches) data.check(matches(withText(text)));
        else data.check(matches(not(withText(text))));
    }

    public void selectTrip(String orig, String dest) {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.orig_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is(orig)))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.dest_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is(dest)))).inRoot(isPlatformPopup()).perform(click());
        onView(withId((R.id.confirm))).perform(click());
        SystemClock.sleep(3000); // Hard code the "waiting" of get request to complete, until I understand Espresso IdlingResource interface...
        onView(withId(R.id.trip_header)).check(matches(withText(orig + " - " + dest)));
    }
}
