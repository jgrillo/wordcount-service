package com.jgrillo.wordcount.core;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MultiSetCounter implements Counter {
    private final Multiset<String> counts;
    private final Integer initialCapacity;

    public MultiSetCounter(Integer initialCapacity) {
        this.counts = HashMultiset.create(initialCapacity);
        this.initialCapacity = initialCapacity;
    }

    @Override
    public synchronized void put(String word) {
        counts.add(word);
    }

    @Override
    public synchronized void putAll(Collection<String> words) {
        counts.addAll(words);
    }

    @Override
    public synchronized Map<String, Long> getCounts() {
        final Map<String, Long> countsMap = new HashMap<>(initialCapacity);
        counts.forEachEntry((s, value) -> countsMap.put(s, Integer.toUnsignedLong(value)));
        return countsMap;
    }
}
