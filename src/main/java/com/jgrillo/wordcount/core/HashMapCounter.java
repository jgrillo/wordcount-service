package com.jgrillo.wordcount.core;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class HashMapCounter implements Counter {
    private final Integer initialCapacity;

    public HashMapCounter(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public Map<String, Long> getCounts(Stream<String> words) {
        final Map<String, Long> counts = new HashMap<>(initialCapacity);

        words.forEach(word -> {
            synchronized (counts) {
                counts.compute(word, (key, value) -> value == null ? 1L : value + 1);
            }
        });

        return counts;
    }
}
