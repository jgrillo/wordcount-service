package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class WordsDeserializer extends StdDeserializer<Words> {
    private static final Logger logger = LoggerFactory.getLogger(WordsDeserializer.class);

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
            return new Words(() -> new WordsIterator(jsonParser));
        } catch (WordsIOEWrapper e) {
            throw e.getCause();
        }
    }
}
