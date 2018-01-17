package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.jgrillo.wordcount.core.Result;

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
    public Words deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        final Iterator<Result<String, IOException>> wordsIterator = new WordsIterator(jsonParser);

        return new Words(wordsIterator);
    }
}
