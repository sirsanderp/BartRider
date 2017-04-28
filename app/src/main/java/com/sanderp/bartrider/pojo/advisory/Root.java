package com.sanderp.bartrider.pojo.advisory;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({
        "@id",
        "uri"
})
@JsonPropertyOrder({
        "date",
        "time",
        "bsa",
        "message"
})
public class Root implements Serializable {
    private final static long serialVersionUID = 592093178472927449L;

    @JsonProperty("date")
    private String date;
    @JsonProperty("time")
    private String time;
    @JsonProperty("bsa")
    private List<Bsa> bsa = null;
    @JsonProperty("message")
    private String message;

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

    public List<Bsa> getBsa() {
        return bsa;
    }

    public void setBsa(List<Bsa> bsa) {
        this.bsa = bsa;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
