package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

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

    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
    @JsonProperty("@fare")
    private String fare;
    @JsonProperty("@origTimeMin")
    private String origTimeMin;
    @JsonProperty("@origTimeDate")
    private String origTimeDate;
    @JsonProperty("@destTimeMin")
    private String destTimeMin;
    @JsonProperty("@destTimeDate")
    private String destTimeDate;
    @JsonProperty("@clipper")
    private String clipper;
    @JsonProperty("@tripTime")
    private String tripTime;
    @JsonProperty("@co2")
    private String co2;
    @JsonProperty("fares")
    private Fares fares;
    @JsonProperty("leg")
    private List<Leg> leg = null;

    @JsonIgnore
    private String etdOrig;
    @JsonIgnore
    private String etaDest;

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

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(String origTimeMin) {
        this.origTimeMin = origTimeMin;
    }

    public String getOrigTimeDate() {
        return origTimeDate;
    }

    public void setOrigTimeDate(String origTimeDate) {
        this.origTimeDate = origTimeDate;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public void setDestTimeMin(String destTimeMin) {
        this.destTimeMin = destTimeMin;
    }

    public String getDestTimeDate() {
        return destTimeDate;
    }

    public void setDestTimeDate(String destTimeDate) {
        this.destTimeDate = destTimeDate;
    }

    public String getClipper() {
        return clipper;
    }

    public void setClipper(String clipper) {
        this.clipper = clipper;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
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

    public String getEtdOrig() {
        return etdOrig;
    }

    public void setEtdOrig(String etdOrig) {
        this.etdOrig = etdOrig;
    }

    public String getEtaDest() {
        return etaDest;
    }

    public void setEtaDest(String etaDest) {
        this.etaDest = etaDest;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
