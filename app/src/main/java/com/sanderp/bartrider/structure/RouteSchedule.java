package com.sanderp.bartrider.structure;

/**
 * Created by Sander on 3/13/2016.
 */
public class RouteSchedule {
    public final String origin;
    public final String originTime;
    public final String destination;
    public final String destinationTime;
    public final String fare;

    public RouteSchedule(String origin, String originTime, String destination, String destTime, String fare) {
        this.origin = origin;
        this.originTime = originTime;
        this.destination = destination;
        this.destinationTime = destTime;
        this.fare = fare;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOriginTime() {
        return originTime;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public String getFare() {
        return fare;
    }
}
