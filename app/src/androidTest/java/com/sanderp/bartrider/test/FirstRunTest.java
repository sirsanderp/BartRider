package com.sanderp.bartrider.test;

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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FirstRunTest {

    private TestUtils utils = new TestUtils();

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class, false, false);

    @Before
    public void setUp() {
        // Clear "firstRun" shared preference and "stations" table.
        utils.clearSharedPrefs(true);
        utils.dropTable(BartRiderContract.Stations.CONTENT_URI);
    }

    @Test
    public void firstRun() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.empty_overview_list)).check(matches(isDisplayed()));
        utils.selectTrip("Castro Valley", "Montgomery St.");
        onView(withId(R.id.trip_planner_layout)).check(doesNotExist());
        onView(withId(R.id.empty_overview_list)).check(matches(not(isDisplayed())));
    }
}
