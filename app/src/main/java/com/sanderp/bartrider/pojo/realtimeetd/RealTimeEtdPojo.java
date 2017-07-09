package com.sanderp.bartrider.pojo.realtimeetd;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@DynamoDBTable(tableName = "REAL_TIME_ETD")
public class RealTimeEtdPojo implements Serializable {
    private static final long serialVersionUID = 2626209002115756111L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);

    private String apiUpdate;
    private String color;
    private String direction;
    private String headAbbr;
    private String headName;
    private String hexcolor;
    private String origAbbr;
    private String origName;
    private int platform;
    private String prevDepart;
    private List<List<Integer>> trains = null;

    @DynamoDBAttribute(attributeName = "api_update")
    public String getApiUpdate() {
        return apiUpdate;
    }

    public void setApiUpdate(String date) {
        this.apiUpdate = date;
    }

    /**
     * Converts apiUpdate time to epoch time.
     * @return the epoch time
     */
    @DynamoDBIgnore
    public long getApiUpdateEpoch() {
        try {
            return DATE_FORMAT.parse(apiUpdate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @DynamoDBAttribute(attributeName = "color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @DynamoDBAttribute(attributeName = "direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @DynamoDBRangeKey(attributeName = "head_abbr")
    public String getHeadAbbr() {
        return headAbbr;
    }

    public void setHeadAbbr(String headAbbr) {
        this.headAbbr = headAbbr;
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

    @DynamoDBHashKey(attributeName = "orig_abbr")
    public String getOrigAbbr() {
        return origAbbr;
    }

    public void setOrigAbbr(String origAbbr) {
        this.origAbbr = origAbbr;
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

    @DynamoDBAttribute(attributeName = "prev_depart")
    public String getPrevDepart() {
        return prevDepart;
    }

    public void setPrevDepart(String prevDepart) {
        this.prevDepart = prevDepart;
    }

    /**
     * Converts prevDepart time to epoch time.
     * @return the epoch time
     */
    @DynamoDBIgnore
    public long getPrevDepartEpoch() {
        try {
            return DATE_FORMAT.parse(prevDepart).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @DynamoDBAttribute(attributeName = "trains")
    public List<List<Integer>> getTrains() {
        return trains;
    }

    public void setTrains(List<List<Integer>> trains) {
        this.trains = trains;
    }

    public int getEtdMinutes(int index) {
        return trains.get(index).get(0);
    }

    public int getEtdSeconds(int index) {
        return trains.get(index).get(1);
    }

    public int getLength(int index) {
        return trains.get(index).get(2);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
