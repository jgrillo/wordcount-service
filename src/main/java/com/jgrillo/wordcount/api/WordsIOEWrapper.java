package com.jgrillo.wordcount.api;

import java.io.IOException;

public final class WordsIOEWrapper extends RuntimeException {
    private final IOException cause;

    public WordsIOEWrapper(String message, IOException cause) {
        super(message, cause);
        this.cause = cause;
    }

    @Override
    public IOException getCause() {
        return cause;
    }
}
