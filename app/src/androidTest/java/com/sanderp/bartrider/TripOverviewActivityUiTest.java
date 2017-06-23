package com.sanderp.bartrider;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.database.BartRiderDbHelper;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TripOverviewActivityUiTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class, false, false);

    @Test
    public void firstRun() {
        // Clear "firstRun" shared preference and "stations" table.
        clearSharedPrefs(true);
        dropTable(BartRiderContract.Stations.CONTENT_URI);

        mActivityRule.launchActivity(null);
        onView(withId(R.id.empty_overview_list)).check(matches(isDisplayed()));
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.trip_planner_layout)).check(doesNotExist());
        onView(withId(R.id.empty_overview_list)).check(matches(not(isDisplayed())));
    }

    @Test
    public void addFirstFavoriteTrip(){
        // Clear the "Favorites" table.
        clearSharedPrefs(false);
        dropTable(BartRiderContract.Favorites.CONTENT_URI);

        mActivityRule.launchActivity(null);
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(isDisplayed()));
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.close());
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.action_favorite)).perform(click());
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(not(isDisplayed())));
    }

    @Test
    public void addFavoriteTrip() {
        // Clear the "Favorites" table.
        clearSharedPrefs(false);
        dropTable(BartRiderContract.Favorites.CONTENT_URI);

        mActivityRule.launchActivity(null);
        selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.action_favorite)).perform(click());
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.action_favorite)).perform(click());
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(0)
                .check(matches(withText("Castro Valley - Ashby")));
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(1)
                .check(matches(withText("Castro Valley - Montgomery St.")));
    }

    @Test
    public void removeFavoriteTrip() {
        // Clear the "Favorites" table.
        clearSharedPrefs(false);
        dropTable(BartRiderContract.Favorites.CONTENT_URI);

        mActivityRule.launchActivity(null);
        selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.action_favorite)).perform(click());
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.action_favorite)).perform(click());
        selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.action_favorite)).perform(click());
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(0)
                .check(matches(not(withText("Castro Valley - Ashby"))));
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(0)
                .check(matches(withText("Castro Valley - Montgomery St.")));
    }

    @Test
    public void reverseTrip() {
        mActivityRule.launchActivity(null);
        selectTrip("Castro Valley", "Ashby");
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_reverse_trip)).perform(click());
        onView(withId(R.id.trip_header)).check(matches(withText("Ashby - Castro Valley")));
    }

    private void clearSharedPrefs(boolean firstRun) {
        SharedPreferences.Editor sharedPrefs = InstrumentationRegistry.getTargetContext().getSharedPreferences(PrefContract.PREFS_NAME, Context.MODE_PRIVATE).edit();
        sharedPrefs.clear();
        if (!firstRun) sharedPrefs.putBoolean(PrefContract.FIRST_RUN, false);
        sharedPrefs.commit();
    }

    private void dropTable(Uri table) {
        getTargetContext().getContentResolver().delete(table, null, null);
    }

    private void selectTrip(String orig, String dest) {
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