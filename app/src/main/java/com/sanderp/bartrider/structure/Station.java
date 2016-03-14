package com.sanderp.bartrider.structure;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sander on 3/13/2016.
 */
public class Station {
    static AtomicInteger nextId = new AtomicInteger();
    private final int id;

    private final String name;
    private final String abbr;
    private final String latitude;
    private final String longitude;
    private final String address;
    private final String city;
    private final String county;
    private final String state;
    private final String zipcode;

    public Station(String name, String abbr, String latitude, String longitude, String address,
                    String city, String county, String state, String zipcode) {
        id = nextId.incrementAndGet();
        this.name = name;
        this.abbr = abbr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.county = county;
        this.state = state;
        this.zipcode = zipcode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }
}