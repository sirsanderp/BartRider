package com.sanderp.bartrider.pojo.stationlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({
        "name",
        "abbr",
        "gtfs_latitude",
        "gtfs_longitude",
        "address",
        "city",
        "county",
        "state",
        "zipcode"
})
public class Station implements Serializable {
    private static final long serialVersionUID = -4636956806771258102L;

    @JsonProperty("name")
    private String name;
    @JsonProperty("abbr")
    private String abbr;
    @JsonProperty("gtfs_latitude")
    private String gtfsLatitude;
    @JsonProperty("gtfs_longitude")
    private String gtfsLongitude;
    @JsonProperty("address")
    private String address;
    @JsonProperty("city")
    private String city;
    @JsonProperty("county")
    private String county;
    @JsonProperty("state")
    private String state;
    @JsonProperty("zipcode")
    private String zipcode;

    static AtomicInteger nextId = new AtomicInteger();
    @JsonIgnore
    private final int id;

    public Station() {
        id = nextId.incrementAndGet();
    }

    public Station(String name, String abbr, String gtfsLatitude, String gtfsLongitude, String address,
                   String city, String county, String state, String zipcode) {
        id = nextId.incrementAndGet();
        this.name = name;
        this.abbr = abbr;
        this.gtfsLatitude = gtfsLatitude;
        this.gtfsLongitude = gtfsLongitude;
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

    public String getGtfsLatitude() {
        return gtfsLatitude;
    }

    public void setGtfsLatitude(String gtfsLatitude) {
        this.gtfsLatitude = gtfsLatitude;
    }

    public String getGtfsLongitude() {
        return gtfsLongitude;
    }

    public void setGtfsLongitude(String gtfsLongitude) {
        this.gtfsLongitude = gtfsLongitude;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
