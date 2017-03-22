package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Words {
    private static final String WORDS_PROP = "words";
    private final List<String> words;

    @JsonCreator
    public Words(@JsonProperty(WORDS_PROP) @NotNull @NotEmpty List<String> words) {
        this.words = words;
    }

    @JsonProperty(WORDS_PROP)
    public List<String> getWords() {
        return words;
    }
}
