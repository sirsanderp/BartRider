package com.sanderp.bartrider.pojo.stationlist;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "station"
})
public class Stations implements Serializable {
    private final static long serialVersionUID = -3252095118707145558L;

    @JsonProperty("station")
    private List<Station> station = null;

    public List<Station> getStation() {
        return station;
    }

    public void setStation(List<Station> station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
