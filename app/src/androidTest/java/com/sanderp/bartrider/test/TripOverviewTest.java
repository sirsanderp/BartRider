package com.sanderp.bartrider.test;

import android.database.Cursor;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.*;
import com.sanderp.bartrider.R;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.utility.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
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
public class TripOverviewTest {

    private TestUtils utils = new TestUtils();

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class, false, false);

    @Before
    public void setUp() {
        utils.clearSharedPrefs(false);
    }

    @Test
    public void addAndRemoveFavoriteTrip() {
        // Clear the "Favorites" table.
        utils.dropTable(BartRiderContract.Favorites.CONTENT_URI);

        mActivityRule.launchActivity(null);
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(isDisplayed()));
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.close());
        utils.selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.action_favorite)).perform(click());
        utils.selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.action_favorite)).perform(click());
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.empty_drawer_list_item)).check(matches(not(isDisplayed())));
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(0)
                .check(matches(withText("Castro Valley - Ashby")));
        onData(instanceOf(Cursor.class))
                .inAdapterView(allOf(withId(R.id.favorites_list_view), isDisplayed()))
                .atPosition(1)
                .check(matches(withText("Castro Valley - Montgomery St.")));
        onView(withId(R.id.trip_overview_drawer_layout)).perform(DrawerActions.close());
        utils.selectTrip("Castro Valley", "Ashby");
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
    public void cancelSelectTrip() {
        mActivityRule.launchActivity(null);
        utils.selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.trip_header)).check(matches(withText("Castro Valley - Ashby")));
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.orig_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is("Castro Valley")))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.dest_spinner)).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)), CursorMatchers.withRowString(BartRiderContract.Stations.Column.NAME, is("Montgomery St.")))).inRoot(isPlatformPopup()).perform(click());
        onView(withId((R.id.cancel))).perform(click());
        onView(withId(R.id.trip_header)).check(matches(withText("Castro Valley - Ashby")));
    }

    @Test
    public void checkAdvisory() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.advisory_layout)).check(doesNotExist());
        onView(withId(R.id.action_advisory)).perform(click());
        onView(withId(R.id.advisory_layout)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.advisory_layout)).check(doesNotExist());
    }

    @Test
    public void checkTripResult() {
        mActivityRule.launchActivity(null);
        utils.selectTrip("Castro Valley", "Ashby");
        onView(withId(R.id.trip_list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_departure_progress_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.next_departure)).check(matches(isDisplayed()));
        onView(withId(R.id.next_departure_window)).check(matches(isDisplayed()));
        onView(withId(R.id.trip_fare)).check(matches(isDisplayed()));
        onView(withId(R.id.trip_fare_info)).check(matches(isDisplayed()));
    }

    @Test
    public void reverseTrip() {
        mActivityRule.launchActivity(null);
        utils.selectTrip("Castro Valley", "Ashby");
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_reverse_trip)).perform(click());
        onView(withId(R.id.trip_header)).check(matches(withText("Ashby - Castro Valley")));
    }
}