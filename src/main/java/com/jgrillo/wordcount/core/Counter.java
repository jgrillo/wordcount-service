package com.jgrillo.wordcount.core;

import java.util.Collection;
import java.util.Map;

public interface Counter {

    void put(String word);

    void putAll(Collection<String> words);

    Map<String, Long> getCounts();
}
