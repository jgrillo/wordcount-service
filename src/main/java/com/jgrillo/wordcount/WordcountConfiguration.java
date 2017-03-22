package com.jgrillo.wordcount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jgrillo.wordcount.core.CounterType;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class WordcountConfiguration extends Configuration {

    @NotNull
    private final CounterType counterType;
    private static final String COUNTER_TYPE_PROP = "counter_type";

    @NotNull
    private final Integer initialCapacity;
    private static final String INITIAL_CAPACITY_PROP = "initial_capacity";

    @JsonCreator
    public WordcountConfiguration(
            @JsonProperty(COUNTER_TYPE_PROP) String counterType,
            @JsonProperty(INITIAL_CAPACITY_PROP) Integer initialCapacity
    ) {
        this.counterType = CounterType.valueOf(counterType.toUpperCase());
        this.initialCapacity = initialCapacity;
    }

    @JsonProperty
    public CounterType getCounterType() {
        return counterType;
    }

    @JsonProperty
    public Integer getInitialCapacity() {
        return initialCapacity;
    }
}
