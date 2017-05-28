package com.sanderp.bartrider.pojo.realtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "minutes",
        "platform",
        "direction",
        "length",
        "color",
        "hexcolor",
        "bikeflag"
})
public class Estimate implements Serializable {
    private static final long serialVersionUID = 1853668132722861521L;

    @JsonProperty("minutes")
    private int minutes;
    @JsonProperty("platform")
    private int platform;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("length")
    private int length;
    @JsonProperty("color")
    private String color;
    @JsonProperty("hexcolor")
    private String hexcolor;
    @JsonProperty("bikeflag")
    private int bikeflag;

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        if (minutes.equals("Leaving")) this.minutes = 0;
        else this.minutes = Integer.parseInt(minutes);
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

    public String getHexcolor() {
        return hexcolor;
    }

    public void setHexcolor(String hexcolor) {
        this.hexcolor = hexcolor;
    }

    public int getBikeflag() {
        return bikeflag;
    }

    public void setBikeflag(int bikeflag) {
        this.bikeflag = bikeflag;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
