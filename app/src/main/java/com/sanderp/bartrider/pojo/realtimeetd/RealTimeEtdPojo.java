package com.sanderp.bartrider.pojo.realtimeetd;

import java.io.Serializable;
import java.util.List;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@DynamoDBTable(tableName = "REAL_TIME_ETD")
public class RealTimeEtdPojo implements Serializable {
    private final static long serialVersionUID = 2626209002115756111L;

    private String color;
    private String date;
    private String direction;
    private List<Integer> etdMinutes = null;
    private List<Integer> etdSeconds = null;
    private String headAbbr;
    private String headName;
    private String hexcolor;
    private List<Integer> lengths = null;
    private String origAbbr;
    private String origName;
    private int platform;
    private long time;

    @DynamoDBHashKey(attributeName = "orig_abbr")
    public String getOrigAbbr() {
        return origAbbr;
    }

    public void setOrigAbbr(String origAbbr) {
        this.origAbbr = origAbbr;
    }

    @DynamoDBRangeKey(attributeName = "head_abbr")
    public String getHeadAbbr() {
        return headAbbr;
    }

    public void setHeadAbbr(String headAbbr) {
        this.headAbbr = headAbbr;
    }

    @DynamoDBAttribute(attributeName = "color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @DynamoDBAttribute(attributeName = "date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName = "direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @DynamoDBAttribute(attributeName = "etd_minutes")
    public List<Integer> getEtdMinutes() {
        return etdMinutes;
    }

    public void setEtdMinutes(List<Integer> etdMinutes) {
        this.etdMinutes = etdMinutes;
    }

    @DynamoDBAttribute(attributeName = "etd_seconds")
    public List<Integer> getEtdSeconds() {
        return etdSeconds;
    }

    public void setEtdSeconds(List<Integer> etdSeconds) {
        this.etdSeconds = etdSeconds;
    }

    @DynamoDBAttribute(attributeName = "head_name")
    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    @DynamoDBAttribute(attributeName = "hexcolor")
    public String getHexcolor() {
        return hexcolor;
    }

    public void setHexcolor(String hexcolor) {
        this.hexcolor = hexcolor;
    }

    @DynamoDBAttribute(attributeName = "lengths")
    public List<Integer> getLengths() {
        return lengths;
    }

    public void setLengths(List<Integer> lengths) {
        this.lengths = lengths;
    }

    @DynamoDBAttribute(attributeName = "orig_name")
    public String getOrigName() {
        return origName;
    }

    public void setOrigName(String origName) {
        this.origName = origName;
    }

    @DynamoDBAttribute(attributeName = "platform")
    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    @DynamoDBAttribute(attributeName = "time")
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
