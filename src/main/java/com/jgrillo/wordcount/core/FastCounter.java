package com.jgrillo.wordcount.core;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FastCounter implements Counter {
    private final int initialCapacity;

    public FastCounter(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public Map<String, Long> getCounts(Stream<String> words) {
        final Map<String, long[]> counts = new HashMap<>(initialCapacity);

        words.iterator().forEachRemaining(word -> {
            final long[] count = counts.get(word);

            if (count == null) {
                counts.put(word, new long[]{1L});
            } else {
                count[0]++;
            }
        });

        return counts.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, (entry) -> entry.getValue()[0]
        ));
    }
}
