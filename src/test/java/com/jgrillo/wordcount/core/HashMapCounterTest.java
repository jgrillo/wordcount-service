package com.jgrillo.wordcount.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.*;

public final class HashMapCounterTest {
    @Test
    public void getCounts() throws Exception {
        qt().forAll(
                lists().allListsOf(strings().allPossible().ofLengthBetween(0, 100)).ofSizeBetween(0, 1000).describedAs(
                        Object::toString
                )
        ).checkAssert((words) -> {
            final Map<String, Long> expectedCounts = new HashMap<>();

            for (final String word: words) {
                final Long count = expectedCounts.getOrDefault(word, 0L);
                expectedCounts.put(word, count + 1);
            }

            final Counter counter = new HashMapCounter(10);
            final Map<String, Long> counts = counter.getCounts(words.parallelStream());

            final List<Map.Entry<String, Long>> expectedCountsEntries = new ArrayList<>(
                    expectedCounts.entrySet()
            );
            expectedCountsEntries.sort(Comparator.comparing(Map.Entry::getKey));

            final List<Map.Entry<String, Long>> countsEntries = new ArrayList<>(
                    counts.entrySet()
            );
            countsEntries.sort(Comparator.comparing(Map.Entry::getKey));

            assertArrayEquals(expectedCountsEntries.toArray(), countsEntries.toArray());
        });
    }
}