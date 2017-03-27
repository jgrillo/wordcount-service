package com.jgrillo.wordcount.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConcurrentHashMapCounter implements Counter {
    private final Integer initialCapacity;

    public ConcurrentHashMapCounter(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public Map<String, Long> getCounts(Stream<String> words) {
        final Map<String, LongAdder> counts = new ConcurrentHashMap<>(initialCapacity);

        words.forEach(word -> {
            final LongAdder adder = counts.get(word);

            if (adder == null) {
                final LongAdder newAdder = new LongAdder();
                final LongAdder oldAdder = counts.putIfAbsent(word, newAdder);

                if (oldAdder == null) {
                    newAdder.increment();
                } else {
                    oldAdder.increment();
                }
            } else {
                adder.increment();
            }
        });

        return counts.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> entry.getValue().sum()
        ));
    }
}
