package com.jgrillo.wordcount.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamCounter implements Counter {
    private final Map<String, Long> counts;

    public StreamCounter(Integer initialCapacity) {
        this.counts = new HashMap<>(initialCapacity);
    }

    @Override
    public synchronized void put(String word) {
        counts.compute(word, (key, value) -> value == null ? 1L : value + 1);
    }

    @Override
    public synchronized void putAll(Collection<String> words) {
        counts.putAll(
                words.parallelStream()
                        .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
        );
    }

    @Override
    public synchronized Map<String, Long> getCounts() {
        return counts;
    }
}
