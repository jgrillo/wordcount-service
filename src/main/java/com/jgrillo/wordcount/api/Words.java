package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonDeserialize(using = WordsDeserializer.class)
public final class Words {
    public static final String WORDS_PROP = "words";
    private final Iterable<String> words;

    @JsonCreator
    public Words(@JsonProperty(WORDS_PROP) @NotNull @NotEmpty Iterable<String> words) {
        this.words = words;
    }

    @JsonProperty(WORDS_PROP)
    public Iterable<String> getWords() {
        return words;
    }
}
