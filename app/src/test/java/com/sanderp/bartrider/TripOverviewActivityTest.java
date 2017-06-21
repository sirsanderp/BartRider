package com.sanderp.bartrider;

import org.junit.Test;

import static org.junit.Assert.*;

public class TripOverviewActivityTest {

    @Test
    public void setTripTest() {
        // Must comment out the Toast in isTripValid() to run tests.
        TripOverviewActivity activity = new TripOverviewActivity();
        assertFalse(activity.isTripSet());
        assertTrue(activity.setTrip("Castro Valley", "CAST", "Montgomery St.", "MONT"));
        assertTrue(activity.isTripSet());
        assertFalse(activity.setTrip("Castro Valley", "CAST", "Montgomery St.", "MONT"));
        assertFalse(activity.setTrip("Castro Valley", "CAST", "Castro Valley", "CAST"));
        assertTrue(activity.setTrip("Montgomery St.", "MONT", "Castro Valley", "CAST"));
    }
}