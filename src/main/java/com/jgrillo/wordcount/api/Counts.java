package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

@JsonSerialize(using = CountsSerializer.class)
public final class Counts {
    public static final String COUNTS_PROP = "counts";
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
