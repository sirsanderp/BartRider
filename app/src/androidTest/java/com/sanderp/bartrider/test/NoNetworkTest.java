package com.sanderp.bartrider.test;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.TripOverviewActivity;
import com.sanderp.bartrider.utility.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NoNetworkTest {

    private TestUtils utils = new TestUtils();

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class, false, false);

    @Test
    public void checkAll() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.trip_planner_layout)).check(doesNotExist());
        onView(withId(R.id.action_advisory)).perform(click());
        onView(withId(R.id.advisory_layout)).check(doesNotExist());
    }
}
