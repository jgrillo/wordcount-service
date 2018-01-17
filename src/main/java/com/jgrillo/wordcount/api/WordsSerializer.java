package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jgrillo.wordcount.core.Result;

import java.io.IOException;
import java.util.Iterator;

public final class WordsSerializer extends StdSerializer<Words> {

    public WordsSerializer() {
        this(Words.class);
    }

    private WordsSerializer(Class<Words> t) {
        super(t);
    }

    @Override
    public void serialize(
            Words words, JsonGenerator jsonGenerator, SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart(Words.WORDS_PROP);

        final Iterator<Result<String, IOException>> wordsIter = words.getWords();
        while(wordsIter.hasNext()) {
            final Result<String, IOException> result = wordsIter.next();
            final String word = result.get();

            if (word != null) {
                jsonGenerator.writeString(word);
            }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
