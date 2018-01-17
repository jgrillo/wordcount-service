package com.jgrillo.wordcount.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public final class HashMapCounter implements Counter {
    private final int initialCapacity;

    public HashMapCounter(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public Map<String, Long> countWords(Iterator<Result<String, IOException>> words) throws IOException{
        final Map<String, long[]> counts = new HashMap<>(initialCapacity);

        for (final Result<String, IOException> result : (Iterable<Result<String, IOException>>)() -> words) { // lolwut
            final String word = result.get();

            if (word != null) {
                final long[] count = counts.get(word);

                if (count == null) {
                    counts.put(word, new long[]{1L});
                } else {
                    count[0]++;
                }
            }
        }

        return counts.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, (entry) -> entry.getValue()[0]
        ));
    }
}
