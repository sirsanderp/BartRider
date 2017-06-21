package com.sanderp.bartrider;

import android.database.Cursor;
import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.database.BartRiderContract;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TripOverviewActivityUiTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class);

    @Test
    public void firstRun() {
        // Application needs to be freshly installed to run this test properly.
        onView(withId(R.id.empty_overview_list_item)).check(matches(isDisplayed()));
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.trip_planner_layout)).check(doesNotExist());
        onView(withId(R.id.empty_overview_list_item)).check(matches(not(isDisplayed())));
    }

    @Test
    public void addFirstFavoriteTrip(){
        // The favorites table must be empty to run this test properly.
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(isDisplayed()));
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.close());
        SystemClock.sleep(5000);
        selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.action_favorite)).perform(click());
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(not(isDisplayed())));
    }

    public void selectTrip(String orig, String dest) {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.orig_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is(orig)))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.dest_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is(dest)))).inRoot(isPlatformPopup()).perform(click());
        onView(withId((R.id.confirm))).perform(click());
        onView(withId(R.id.trip_header)).check(matches(withText(orig + " - " + dest)));
    }
}