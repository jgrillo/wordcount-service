package com.jgrillo.wordcount.core;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@FunctionalInterface
public interface Counter {
    Map<String, Long> countWords(Iterator<Result<String, IOException>> words) throws IOException;
}
