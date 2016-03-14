package com.sanderp.bartrider.structure;

/**
 * Created by Sander on 3/13/2016.
 */
public class RouteSchedule {
    public final String orig;
    public final String orig_time;
    public final String dest;
    public final String dest_time;
    public final String fare;

    public RouteSchedule(String orig, String orig_time, String dest, String dest_time, String fare) {
        this.orig = orig;
        this.orig_time = orig_time;
        this.dest = dest;
        this.dest_time = dest_time;
        this.fare = fare;
    }

    public String getOrig() {
        return orig;
    }

    public String getOrig_time() {
        return orig_time;
    }

    public String getDest() {
        return dest;
    }

    public String getDest_time() {
        return dest_time;
    }

    public String getFare() {
        return fare;
    }
}
