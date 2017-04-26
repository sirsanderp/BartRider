package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@amount",
        "@class"
})
class Fare implements Serializable {
    private final static long serialVersionUID = 4593250643945599573L;

    @JsonProperty("@amount")
    private String amount;
    @JsonProperty("@class")
    private String _class;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
