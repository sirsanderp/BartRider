package com.sanderp.bartrider.structure;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sander Peerna on 3/13/2016.
 */
public class Station {
    static AtomicInteger nextId = new AtomicInteger();
    private final int id;

    // Station attributes
    private String name;
    private String abbr;
    private String latitude;
    private String longitude;
    private String address;
    private String city;
    private String county;
    private String state;
    private String zipcode;

    public Station() {
        id = nextId.incrementAndGet();
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipcode(String zipcode) {
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