package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class CountsProcessingException extends JsonProcessingException {
    protected CountsProcessingException(String msg, JsonLocation loc) {
        super(msg, loc);
    }
}
