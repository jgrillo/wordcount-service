package com.jgrillo.wordcount.core;

import java.util.Map;
import java.util.stream.Stream;

@FunctionalInterface
public interface Counter {
    Map<String, Long> getCounts(Stream<String> words);
}
