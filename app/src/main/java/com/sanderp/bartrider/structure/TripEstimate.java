package com.sanderp.bartrider.structure;

import java.io.Serializable;

@Deprecated
public class TripEstimate implements Serializable {
    // Matches tags within the <estimate> tag.
    private String date;
    private String time;
    private int minutes;
    private int platform;
    private String direction;
    private int length;
    private String color;
    private String hexColor;
    private boolean bikeFlag;

    public TripEstimate() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public boolean isBikeFlag() {
        return bikeFlag;
    }

    public void setBikeFlag(boolean bikeFlag) {
        this.bikeFlag = bikeFlag;
    }
}
