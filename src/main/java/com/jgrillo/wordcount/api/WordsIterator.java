package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class WordsIterator implements Iterator<String> {
    private final JsonParser jsonParser;

    private boolean started = false;
    private JsonToken token;

    WordsIterator(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
        this.token = jsonParser.getCurrentToken();
    }

    @Override
    public boolean hasNext() {
        return !jsonParser.isClosed();
    }

    @Override
    public String next() {
        try {
            if (jsonParser.isClosed()) {
                throw new NoSuchElementException("All outta words!");
            } else {
                // advance to the start of the words array -- this loop runs during the first next() call
                while (!started) {
                    if (token == null) {
                        token = jsonParser.nextToken();
                        continue;
                    }

                    switch (token) {
                        case START_OBJECT:
                            token = jsonParser.nextToken();
                            break;
                        case NOT_AVAILABLE:

                        case FIELD_NAME:
                            final String name = jsonParser.getCurrentName();

                            if (name.equals(Words.WORDS_PROP)) {
                                token = jsonParser.nextToken();
                            } else {
                                throw new WordsProcessingException(
                                        String.format("Encountered unknown field: \"%s\"", name),
                                        jsonParser.getCurrentLocation()
                                );
                            }
                            break;
                        case START_ARRAY:
                            token = jsonParser.nextToken();
                            started = true;
                            break;
                        default:
                            throw new WordsProcessingException(
                                    "Encountered unknown state.", jsonParser.getCurrentLocation()
                            );
                    }
                }

                // We are in the array, so return the current value
                switch (token) {
                    case VALUE_STRING:
                        final String word = jsonParser.getValueAsString();

                        token = jsonParser.nextToken(); // peek ahead
                        if (token.equals(JsonToken.END_ARRAY)) {
                            jsonParser.close();
                        }

                        return word;
                    default:
                        throw new WordsProcessingException(
                                "Encountered unknown state.", jsonParser.getCurrentLocation()
                        );
                }
            }
        } catch (IOException e) {
            throw new WordsIOEWrapper("Caught IOException while deserializing words.", e);
        }
    }
}
