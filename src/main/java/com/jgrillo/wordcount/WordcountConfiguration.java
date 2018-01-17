package com.jgrillo.wordcount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public final class WordcountConfiguration extends Configuration implements Config {

    @NotNull
    private final int initialCapacity;
    private static final String INITIAL_CAPACITY_PROP = "initial_capacity";

    @JsonCreator
    public WordcountConfiguration(@JsonProperty(INITIAL_CAPACITY_PROP) int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    @JsonProperty(INITIAL_CAPACITY_PROP)
    public int getInitialCapacity() {
        return initialCapacity;
    }
}
