package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sanderp.bartrider.utility.Utils;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private static final long serialVersionUID = 4533555934408239702L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    @JsonProperty("@order")
    private int order;
    @JsonProperty("@transfercode")
    private String transferCode;
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
    private int bikeFlag;
    @JsonProperty("@trainHeadStation")
    private String trainHeadStation;
    @JsonProperty("@load")
    private int load;
    @JsonProperty("@trainId")
    private long trainId;
    @JsonProperty("@trainIdx")
    private int trainIdx;

    @JsonIgnore
    private String originFull;
    @JsonIgnore
    private String destinationFull;
    @JsonIgnore
    private long etdOrigTime;
    @JsonIgnore
    private long etdDestTime;
    @JsonIgnore
    private int length = 0;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(String transferCode) {
        this.transferCode = transferCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
        setOriginFull(origin);
    }

    public String getOriginFull() {
        return originFull;
    }

    public void setOriginFull(String abbr) {
        this.originFull = Utils.getStationFull(abbr);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
        setDestinationFull(destination);
    }

    public String getDestinationFull() {
        return destinationFull;
    }

    public void setDestinationFull(String abbr) {
        this.destinationFull = Utils.getStationFull(abbr);;
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
        try {
            if ("12:00 AM".equals(origTimeMin)) {
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Calendar c = Calendar.getInstance();
                c.setTime(df.parse(origTimeDate));
                c.add(Calendar.DATE, 1);
                this.origTimeDate = df.format(c.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setEtdOrigTime(getOrigTimeEpoch());
    }

    /**
     * Converts leg origin time and date to epoch time.
     * @return the epoch time
     */
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
        try {
            if ("12:00 AM".equals(destTimeMin)) {
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Calendar c = Calendar.getInstance();
                c.setTime(df.parse(destTimeDate));
                c.add(Calendar.DATE, 1);
                this.destTimeDate = df.format(c.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setEtdDestTime(getDestTimeEpoch());
    }

    /**
     * Converts leg destination time and date to epoch time.
     * @return the epoch time
     */
    public long getDestTimeEpoch() {
        try {
            return DATE_FORMAT.parse(destTimeDate + " " + destTimeMin).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public int getUntilOrigDepart() {
        return (int) ((getOrigTimeEpoch() - new Date().getTime()) / 1000);
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getBikeFlag() {
        return bikeFlag;
    }

    public void setBikeFlag(int bikeFlag) {
        this.bikeFlag = bikeFlag;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getTripTime() {
        return etdDestTime - etdOrigTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
