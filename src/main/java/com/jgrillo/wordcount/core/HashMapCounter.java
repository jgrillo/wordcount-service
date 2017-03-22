package com.jgrillo.wordcount.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMapCounter implements Counter {
    private final Map<String, Long> counts;

    public HashMapCounter(Integer initialCapacity) {
        this.counts = new HashMap<>(initialCapacity);
    }

    @Override
    public synchronized void put(String word) {
        counts.compute(word, (key, value) -> value == null ? 1L : value + 1);
    }

    @Override
    public synchronized void putAll(Collection<String> words) {
        words.forEach(this::put);
    }

    @Override
    public synchronized Map<String, Long> getCounts() {
        return counts;
    }
}
