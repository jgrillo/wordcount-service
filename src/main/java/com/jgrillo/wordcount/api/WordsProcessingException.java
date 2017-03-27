package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class WordsProcessingException extends JsonProcessingException {
    protected WordsProcessingException(String msg, JsonLocation loc) {
        super(msg, loc);
    }
}
