package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sanderp.bartrider.pojo.TimeToLongDeserializer;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@order",
        "@transfercode",
        "@origin",
        "@destination",
        "@origTimeMin",
        "@origTimeDate",
        "@destTimeMin",
        "@destTimeDate",
        "@line",
        "@bikeflag",
        "@trainHeadStation",
        "@load",
        "@trainId",
        "@trainIdx"
})
public class Leg implements Serializable {
    private final static long serialVersionUID = 4533555934408239702L;

    @JsonProperty("@order")
    private int order;
    @JsonProperty("@transfercode")
    private String transfercode;
    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
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
    @JsonProperty("@line")
    private String line;
    @JsonProperty("@bikeflag")
    private int bikeflag;
    @JsonProperty("@trainHeadStation")
    private String trainHeadStation;
    @JsonProperty("@load")
    private int load;
    @JsonProperty("@trainId")
    private long trainId;
    @JsonProperty("@trainIdx")
    private int trainIdx;

    @JsonIgnore
    private long etdOrigTimeMin;
    @JsonIgnore
    private long etdDestTimeMin;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTransfercode() {
        return transfercode;
    }

    public void setTransfercode(String transfercode) {
        this.transfercode = transfercode;
    }

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

    public long getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(long origTimeMin) {
        this.origTimeMin = origTimeMin;
        setEtdOrigTimeMin(origTimeMin);
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
        setEtdDestTimeMin(destTimeMin);
    }

    public String getDestTimeDate() {
        return destTimeDate;
    }

    public void setDestTimeDate(String destTimeDate) {
        this.destTimeDate = destTimeDate;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getBikeflag() {
        return bikeflag;
    }

    public void setBikeflag(int bikeflag) {
        this.bikeflag = bikeflag;
    }

    public String getTrainHeadStation() {
        return trainHeadStation;
    }

    public void setTrainHeadStation(String trainHeadStation) {
        this.trainHeadStation = trainHeadStation;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public long getTrainId() {
        return trainId;
    }

    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }

    public int getTrainIdx() {
        return trainIdx;
    }

    public void setTrainIdx(int trainIdx) {
        this.trainIdx = trainIdx;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
