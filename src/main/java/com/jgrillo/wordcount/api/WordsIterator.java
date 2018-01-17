package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.jgrillo.wordcount.core.Result;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class WordsIterator implements Iterator<Result<String, IOException>> {
    private final JsonParser jsonParser;

    private JsonToken token;
    private boolean started;
    private boolean stopped;

    WordsIterator(JsonParser jsonParser) {
        this.jsonParser = jsonParser.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        this.token = jsonParser.getCurrentToken();
        this.started = false;
        this.stopped = false;
    }

    @Override
    public boolean hasNext() {
        //return !stopped && !jsonParser.isClosed();
        return !stopped;
    }

    @Override
    public Result<String, IOException> next() {
        // advance to the start of the words array -- this loop runs during the first next() call
        try {
            while (!started) {
                if (token == null) {
                    token = jsonParser.nextToken();
                    continue;
                }

                switch (token) {
                    case START_OBJECT:
                        token = jsonParser.nextToken();
                        break;
                    case FIELD_NAME:
                        final String name = jsonParser.getCurrentName();

                        if (name.equals(Words.WORDS_PROP)) {
                            token = jsonParser.nextToken();
                            break;
                        } else {
                            return () -> {
                                throw new WordsProcessingException(
                                        String.format("Encountered unknown field: \"%s\"", name),
                                        jsonParser.getCurrentLocation()
                                );
                            };
                        }
                    case START_ARRAY:
                        token = jsonParser.nextToken();
                        started = true;

                        break;
                    default:
                        return () -> {
                            throw new WordsProcessingException("Malformed JSON", jsonParser.getCurrentLocation());
                        };
                }
            }

            // We are in the array, so return the current value
            switch (token) {
                case VALUE_STRING:
                    final String word;
                    word = jsonParser.getValueAsString();

                    token = jsonParser.nextToken(); // peek ahead

                    if (token == null || token.equals(JsonToken.END_ARRAY)) {
                        stopped = true;
                    }

                    return () -> word;
                case END_ARRAY:
                    if (stopped) {
                        return () -> {
                            throw new NoSuchElementException("Words array has been exhausted");
                        };
                    } else { // we'll encounter this branch parsing the empty payload: {"words": []}
                        stopped = true;
                        return () -> null;
                    }
                default:
                    return () -> {
                        throw new WordsProcessingException("Malformed JSON", jsonParser.getCurrentLocation());
                    };
            }
        } catch (IOException e) {
            return () -> {
                throw e;
            };
        }
    }
}
