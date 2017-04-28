package com.sanderp.bartrider.pojo.advisory;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({
        "?xml"
})
@JsonPropertyOrder({
        "root"
})
public class AdvisoryPojo implements Serializable {
    private final static long serialVersionUID = 3811606788436490021L;

    @JsonProperty("root")
    private Root root;

    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
