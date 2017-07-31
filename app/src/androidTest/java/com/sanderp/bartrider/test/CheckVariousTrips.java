package com.sanderp.bartrider.test;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sanderp.bartrider.TripOverviewActivity;
import com.sanderp.bartrider.utility.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckVariousTrips {

    private TestUtils utils = new TestUtils();

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(TripOverviewActivity.class, false, false);

    @Test
    public void selectTrips() {
        mActivityRule.launchActivity(null);
        utils.selectTrip("Castro Valley", "Bay Fair");
        utils.selectTrip("Castro Valley", "Rockridge");
        utils.selectTrip("Castro Valley", "Warm Springs / South Fremont");
        utils.selectTrip("Concord", "Balboa Park");
        utils.selectTrip("Concord", "Fruitvale");
        utils.selectTrip("Concord", "Millbrae");
        utils.selectTrip("Concord", "Richmond");
        utils.selectTrip("Concord", "San Francisco International Airport");
        utils.selectTrip("Oakland International Airport", "Colma");
        utils.selectTrip("North Berkeley", "Hayward");
        utils.selectTrip("North Berkeley", "West Dublin/Pleasanton");
        utils.selectTrip("MacArthur", "Bay Fair");
        utils.selectTrip("MacArthur", "Powell St.");
        utils.selectTrip("Millbrae", "Bay Fair");
        utils.selectTrip("San Francisco International Airport", "Bay Fair");
        utils.selectTrip("San Bruno", "Montgomery St.");
        utils.selectTrip("West Oakland", "Oakland International Airport");
//        onData(instanceOf(Trip.class)).inAdapterView(allOf(withId(com.sanderp.bartrider.R.id.trip_list_view), isDisplayed())).atPosition(0).perform(click());
    }
}
