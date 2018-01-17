package com.jgrillo.wordcount.core;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jgrillo.wordcount.WordcountTestUtil.wordsIterator;
import static org.junit.Assert.*;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;

public final class HashMapCounterTest {
    @Test
    public void getCounts() throws Exception {
        qt().forAll(
                lists().of(strings().allPossible().ofLengthBetween(0, 100)).ofSizeBetween(0, 1000).describedAs(
                        Object::toString
                )
        ).checkAssert((words) -> {
            final Map<String, Long> expectedCounts = new HashMap<>();

            for (final String word: words) {
                final Long count = expectedCounts.getOrDefault(word, 0L);
                expectedCounts.put(word, count + 1);
            }

            final Counter counter = new HashMapCounter(10);

            final Map<String, Long> counts;
            try {
                counts = counter.countWords(wordsIterator(words));
            } catch (IOException e) {
                fail(e.getMessage());
                return; // lol weird
            }

            final List<Map.Entry<String, Long>> expectedCountsEntries = new ArrayList<>(expectedCounts.entrySet());
            expectedCountsEntries.sort(Comparator.comparing(Map.Entry::getKey));

            final List<Map.Entry<String, Long>> countsEntries = new ArrayList<>(counts.entrySet());
            countsEntries.sort(Comparator.comparing(Map.Entry::getKey));

            assertArrayEquals(expectedCountsEntries.toArray(), countsEntries.toArray());
        });
    }
}
