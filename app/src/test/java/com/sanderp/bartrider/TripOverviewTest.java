package com.sanderp.bartrider;

import org.junit.Test;

import static org.junit.Assert.*;

public class TripOverviewTest {

    @Test
    public void setTrip() {
        // Must comment out the Toast in isTripValid() of TripOverviewActivity to run tests.
        TripOverviewActivity activity = new TripOverviewActivity();
        assertFalse(activity.isTripSet());
        activity.setTrip(0, "Castro Valley", "CAST", "Montgomery St.", "MONT");
        assertTrue(activity.isTripSet());
    }
}