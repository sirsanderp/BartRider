package com.sanderp.bartrider.structure;

import java.util.concurrent.atomic.AtomicInteger;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}