package com.jgrillo.wordcount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jgrillo.wordcount.core.CounterType;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public final class WordcountConfiguration extends Configuration implements Config {

    @NotNull
    private final CounterType counterType;
    private static final String COUNTER_TYPE_PROP = "counter_type";

    @NotNull
    private final int initialCapacity;
    private static final String INITIAL_CAPACITY_PROP = "initial_capacity";

    @NotNull
    private final boolean parallel;
    private static final String PARALLEL_PROP = "parallel";

    @JsonCreator
    public WordcountConfiguration(
            @JsonProperty(COUNTER_TYPE_PROP) String counterType,
            @JsonProperty(INITIAL_CAPACITY_PROP) int initialCapacity,
            @JsonProperty(PARALLEL_PROP) boolean parallel
    ) {
        this.counterType = CounterType.valueOf(counterType.toUpperCase());
        this.initialCapacity = initialCapacity;
        this.parallel = parallel;
    }

    @Override
    @JsonProperty(COUNTER_TYPE_PROP)
    public CounterType getCounterType() {
        return counterType;
    }

    @Override
    @JsonProperty(INITIAL_CAPACITY_PROP)
    public int getInitialCapacity() {
        return initialCapacity;
    }

    @Override
    @JsonProperty(PARALLEL_PROP)
    public boolean getParallel() {
        return parallel;
    }
}
