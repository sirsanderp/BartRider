package com.sanderp.bartrider.structure;

/**
 * Created by sande on 11/11/2016.
 */

public class TripEstimate {
    private int minutes;
    private int platform;
    private String direction;
    private int length;
    private String color;
    private String hexColor;
    private boolean bikeFlag;

    public TripEstimate() {}

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public void setBikeFlag(boolean bikeFlag) {
        this.bikeFlag = bikeFlag;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getPlatform() {
        return platform;
    }

    public String getDirection() {
        return direction;
    }

    public int getLength() {
        return length;
    }

    public String getColor() {
        return color;
    }

    public String getHexColor() {
        return hexColor;
    }

    public boolean isBikeFlag() {
        return bikeFlag;
    }
}
