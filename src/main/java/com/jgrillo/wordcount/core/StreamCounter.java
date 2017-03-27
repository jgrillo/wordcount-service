package com.jgrillo.wordcount.core;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamCounter implements Counter {
    @Override
    public Map<String, Long> getCounts(Stream<String> words) {
        return words.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }
}
