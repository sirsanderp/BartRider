package com.sanderp.bartrider.pojo.quickplanner;

import android.graphics.Color;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
    private static final long serialVersionUID = 4533555934408239702L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
    private static final HashMap<String, String> ROUTE_COLORS = new HashMap<String, String>() {
        {
            put("ROUTE 1", "#ffffff33");
            put("ROUTE 2", "#ffffff33");
            put("ROUTE 3", "#ffff9933");
            put("ROUTE 4", "#ffff9933");
            put("ROUTE 5", "#ff339933");
            put("ROUTE 6", "#ff339933");
            put("ROUTE 7", "#ffff0000");
            put("ROUTE 8", "#ffff0000");
            put("ROUTE 9", "#ffffff33");
            put("ROUTE 10", "#ffffff33");
            put("ROUTE 11", "#ff0099cc");
            put("ROUTE 12", "#ff0099cc");
            put("ROUTE 19", "#ffd5cfa3");
            put("ROUTE 20", "#ffd5cfa3");
        }
    };

    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
    @JsonProperty("@fare")
    private float fare;
    @JsonProperty("@origTimeMin")
    private String origTimeMin;
    @JsonProperty("@origTimeDate")
    private String origTimeDate;
    @JsonProperty("@destTimeMin")
    private String destTimeMin;
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
    private List<Leg> legs = null;

    @JsonIgnore
    private long etdOrigTime;
    @JsonIgnore
    private long etdDestTime;
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
        setEtdOrigTime(getOrigTimeEpoch());
    }

    public long getOrigTimeEpoch() {
        try {
            return DATE_FORMAT.parse(origTimeDate + " " + origTimeMin).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
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
        setEtdDestTime(getDestTimeEpoch());
    }

    public long getDestTimeEpoch() {
        try {
            return DATE_FORMAT.parse(destTimeDate + " " + destTimeMin).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
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

    public Leg getLeg(int index) {
        return legs.get(index);
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
        buildRouteColors();
    }

    public long getEtdOrigTime() {
        return etdOrigTime;
    }

    public void setEtdOrigTime(long etdOrigTime) {
        this.etdOrigTime = etdOrigTime;
    }

    public long getEtdDestTime() {
        return etdDestTime;
    }

    public void setEtdDestTime(long etdDestTime) {
        this.etdDestTime = etdDestTime;
    }

    public int[] getRouteColors() {
        return routeColors;
    }

    public void setRouteColors(int[] routeColors) {
        this.routeColors = routeColors;
    }

    private void buildRouteColors() {
        int[] routeColors = new int[getLegs().size()];
        for (int leg = 0; leg < getLegs().size(); leg++) {
            routeColors[leg] = Color.parseColor(ROUTE_COLORS.get(getLegs().get(leg).getLine()));
        }
        setRouteColors(routeColors);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
