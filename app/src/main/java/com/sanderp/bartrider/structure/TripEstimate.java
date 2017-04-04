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
