package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private String order;
    @JsonProperty("@transfercode")
    private String transfercode;
    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
    @JsonProperty("@origTimeMin")
    private String origTimeMin;
    @JsonProperty("@origTimeDate")
    private String origTimeDate;
    @JsonProperty("@destTimeMin")
    private String destTimeMin;
    @JsonProperty("@destTimeDate")
    private String destTimeDate;
    @JsonProperty("@line")
    private String line;
    @JsonProperty("@bikeflag")
    private String bikeflag;
    @JsonProperty("@trainHeadStation")
    private String trainHeadStation;
    @JsonProperty("@load")
    private String load;
    @JsonProperty("@trainId")
    private String trainId;
    @JsonProperty("@trainIdx")
    private String trainIdx;

    @JsonIgnore
    private String etdLegOrig;
    @JsonIgnore
    private String etaLegDest;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
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

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getBikeflag() {
        return bikeflag;
    }

    public void setBikeflag(String bikeflag) {
        this.bikeflag = bikeflag;
    }

    public String getTrainHeadStation() {
        return trainHeadStation;
    }

    public void setTrainHeadStation(String trainHeadStation) {
        this.trainHeadStation = trainHeadStation;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainIdx() {
        return trainIdx;
    }

    public void setTrainIdx(String trainIdx) {
        this.trainIdx = trainIdx;
    }

    public String getEtdLegOrig() {
        return etdLegOrig;
    }

    public void setEtdLegOrig(String etdLegOrig) {
        this.etdLegOrig = etdLegOrig;
    }

    public String getEtaLegDest() {
        return etaLegDest;
    }

    public void setEtaLegDest(String etaLegDest) {
        this.etaLegDest = etaLegDest;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
