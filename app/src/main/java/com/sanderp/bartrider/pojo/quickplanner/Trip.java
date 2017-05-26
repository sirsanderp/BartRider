package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sanderp.bartrider.pojo.TimeToLongDeserializer;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@origin",
        "@destination",
        "@fare",
        "@origTimeMin",
        "@origTimeDate",
        "@destTimeMin",
        "@destTimeDate",
        "@clipper",
        "@tripTime",
        "@co2",
        "fares",
        "leg"
})
public class Trip implements Serializable {
    private final static long serialVersionUID = 4533555934408239702L;
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
    @JsonProperty("@fare")
    private float fare;
    @JsonProperty("@origTimeMin")
    @JsonDeserialize(using = TimeToLongDeserializer.class)
    private long origTimeMin;
    @JsonProperty("@origTimeDate")
    private String origTimeDate;
    @JsonProperty("@destTimeMin")
    @JsonDeserialize(using = TimeToLongDeserializer.class)
    private long destTimeMin;
    @JsonProperty("@destTimeDate")
    private String destTimeDate;
    @JsonProperty("@clipper")
    private float clipper;
    @JsonProperty("@tripTime")
    private int tripTime;
    @JsonProperty("@co2")
    private float co2;
    @JsonProperty("fares")
    private Fares fares;
    @JsonProperty("leg")
    private List<Leg> leg = null;

    @JsonIgnore
    private long etdOrigTimeMin;
    @JsonIgnore
    private long etdDestTimeMin;
    @JsonIgnore
    private int[] routeColors;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }

    public long getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(long origTimeMin) {
        this.origTimeMin = origTimeMin;
        setEtdOrigTimeMin(this.origTimeMin);
    }

    public String getOrigTimeDate() {
        return origTimeDate;
    }

    public void setOrigTimeDate(String origTimeDate) {
        this.origTimeDate = origTimeDate;
    }

    public long getDestTimeMin() {
        return destTimeMin;
    }

    public void setDestTimeMin(long destTimeMin) {
        this.destTimeMin = destTimeMin;
        setEtdDestTimeMin(this.destTimeMin);
    }

    public String getDestTimeDate() {
        return destTimeDate;
    }

    public void setDestTimeDate(String destTimeDate) {
        this.destTimeDate = destTimeDate;
    }

    public float getClipper() {
        return clipper;
    }

    public void setClipper(float clipper) {
        this.clipper = clipper;
    }

    public int getTripTime() {
        return tripTime;
    }

    public void setTripTime(int tripTime) {
        this.tripTime = tripTime;
    }

    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public Fares getFares() {
        return fares;
    }

    public void setFares(Fares fares) {
        this.fares = fares;
    }

    public List<Leg> getLeg() {
        return leg;
    }

    public void setLeg(List<Leg> leg) {
        this.leg = leg;
    }

    public long getEtdOrigTimeMin() {
        return etdOrigTimeMin;
    }

    public void setEtdOrigTimeMin(long etdOrigTimeMin) {
        this.etdOrigTimeMin = etdOrigTimeMin;
    }

    public long getEtdDestTimeMin() {
        return etdDestTimeMin;
    }

    public void setEtdDestTimeMin(long etdDestTimeMin) {
        this.etdDestTimeMin = etdDestTimeMin;
    }

    public int[] getRouteColors() {
        return routeColors;
    }

    public void setRouteColors(int[] routeColors) {
        this.routeColors = routeColors;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
