package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({
        "@amount",
        "@class"
})
public class Fare implements Serializable {
    private static final long serialVersionUID = 4593250643945599573L;

    @JsonProperty("@amount")
    private float amount;
    @JsonProperty("@class")
    private String fareClass;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getClass_() {
        return fareClass;
    }

    public void setClass_(String _class) {
        this.fareClass = _class;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
