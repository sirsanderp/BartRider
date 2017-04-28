package com.sanderp.bartrider.pojo.advisory;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "#cdata-section"
})
public class SmsText implements Serializable {
    private final static long serialVersionUID = 4109235111446960119L;

    @JsonProperty("#cdata-section")
    private String cdataSection;

    public String getCdataSection() {
        return cdataSection;
    }

    public void setCdataSection(String cdataSection) {
        this.cdataSection = cdataSection;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}