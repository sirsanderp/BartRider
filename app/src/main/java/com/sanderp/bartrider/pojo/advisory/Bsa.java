package com.sanderp.bartrider.pojo.advisory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@id",
        "station",
        "type",
        "description",
        "sms_text",
        "posted",
        "expires"
})
public class Bsa implements Serializable {
    private static final long serialVersionUID = -7515140446250679961L;

    @JsonProperty("@id")
    private long id;
    @JsonProperty("station")
    private String station;
    @JsonProperty("type")
    private String type;
    @JsonProperty("description")
    private Description description;
    @JsonProperty("sms_text")
    private SmsText smsText;
    @JsonProperty("posted")
    private Date posted;
    @JsonProperty("expires")
    private Date expires;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public SmsText getSmsText() {
        return smsText;
    }

    public void setSmsText(SmsText smsText) {
        this.smsText = smsText;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
