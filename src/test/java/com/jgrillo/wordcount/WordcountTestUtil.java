package com.jgrillo.wordcount;

import com.jgrillo.wordcount.core.Result;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class WordcountTestUtil {

    public static Iterator<Result<String, IOException>> wordsIterator(Iterable<String> words) {
        return StreamSupport.stream(words.spliterator(), false)
                .map((word) -> (Result<String, IOException>) () -> word)
                .iterator();
    }
}
