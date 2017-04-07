package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.Iterator;

public final class WordsIterator implements Iterator<String> {
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
        return !stopped && !jsonParser.isClosed();
    }

    @Override
    public String next() {
        // advance to the start of the words array -- this loop runs during the first next() call
        while (!started) {
            if (token == null) {
                try {
                    token = jsonParser.nextToken();
                    continue;
                } catch (IOException e) {
                    throw new WordsIOEWrapper("Caught IOE while fetching next token", e);
                }
            }

            switch (token) {
                case START_OBJECT:
                    try {
                        token = jsonParser.nextToken();
                        break;
                    } catch (IOException e) {
                        throw new WordsIOEWrapper("Caught IOE while fetching next token", e);
                    }
                case FIELD_NAME:
                    try {
                        final String name = jsonParser.getCurrentName();

                        if (name.equals(Words.WORDS_PROP)) {
                            token = jsonParser.nextToken();
                            break;
                        } else {
                            throw new WordsIOEWrapper("Unknown field", new WordsProcessingException(
                                    String.format("Encountered unknown field: \"%s\"", name),
                                    jsonParser.getCurrentLocation()
                            ));
                        }
                    } catch (IOException e) {
                        throw new WordsIOEWrapper("Caught IOE while fetching next token", e);
                    }
                case START_ARRAY:
                    try {
                        token = jsonParser.nextToken();
                        started = true;
                        break;
                    } catch (IOException e) {
                        throw new WordsIOEWrapper("Caught IOE while fetching next token", e);
                    }
                default:
                    throw new WordsIOEWrapper("Unknown state", new WordsProcessingException(
                            "Malformed JSON", jsonParser.getCurrentLocation()
                    ));
            }
        }

        // We are in the array, so return the current value
        switch (token) {
            case VALUE_STRING:
                final String word;
                try {
                    word = jsonParser.getValueAsString();

                    try {
                        token = jsonParser.nextToken(); // peek ahead
                    } catch (IOException e) {
                        throw new WordsIOEWrapper("Caught IOE while fetching next token", e);
                    }

                    if (token == null || token.equals(JsonToken.END_ARRAY)) {
                        stopped = true;
                    }

                    return word;
                } catch (IOException e) {
                    throw new WordsIOEWrapper("Caught IOE while fetching string value", e);
                }
            case END_ARRAY:
                stopped = true;
                return null;
            default:
                throw new WordsIOEWrapper("Unknown state", new WordsProcessingException(
                        "Malformed JSON", jsonParser.getCurrentLocation()
                ));
        }
    }
}
