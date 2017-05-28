package com.sanderp.bartrider.pojo.advisory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "#cdata-section"
})
public class Description implements Serializable {
    private static final long serialVersionUID = 6750578806904936058L;

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
