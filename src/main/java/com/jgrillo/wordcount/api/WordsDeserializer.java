package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class WordsDeserializer extends StdDeserializer<Words> {

    public WordsDeserializer() {
        this(Words.class);
    }

    private WordsDeserializer(Class<Words> t) {
        super(t);
    }

    @Override
    public Words deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext
    ) throws IOException {
        try {
            final Iterator<String> wordsIterator = new WordsIterator(jsonParser);
            final Spliterator<String> wordsSpliterator = Spliterators.spliteratorUnknownSize(wordsIterator, 0);
            final Stream<String> wordsStream = StreamSupport.stream(wordsSpliterator, true)
                    .filter(Objects::nonNull);

            return new Words(wordsStream);
        } catch (WordsIOEWrapper e) {
            throw e.getCause();
        }
    }
}
