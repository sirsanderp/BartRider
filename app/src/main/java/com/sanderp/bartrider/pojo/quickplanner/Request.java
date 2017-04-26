package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "trip"
})
public class Request implements Serializable {
    private final static long serialVersionUID = 7493110658643323575L;

    @JsonProperty("trip")
    private List<Trip> trip = null;

    public List<Trip> getTrip() {
        return trip;
    }

    public void setTrip(List<Trip> trip) {
        this.trip = trip;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
