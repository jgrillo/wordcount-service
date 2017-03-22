package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class Counts {
    private static final String COUNTS_PROP = "counts";
    private final Map<String, Long> counts;

    @JsonCreator
    public Counts(@JsonProperty(COUNTS_PROP) @NotNull @NotEmpty Map<String, Long> counts) {
        this.counts = counts;
    }

    @JsonProperty(COUNTS_PROP)
    public Map<String, Long> getCounts() {
        return counts;
    }
}
