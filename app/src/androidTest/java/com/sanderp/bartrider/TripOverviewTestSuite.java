package com.sanderp.bartrider;

import com.sanderp.bartrider.test.FirstRunTest;
import com.sanderp.bartrider.test.TripOverviewTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FirstRunTest.class,
        TripOverviewTest.class
})
public class TripOverviewTestSuite {
}
