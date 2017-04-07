package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@JsonDeserialize(using = WordsDeserializer.class)
@JsonSerialize(using = WordsSerializer.class)
public final class Words {
    public static final String WORDS_PROP = "words";
    private final Stream<String> words;

    @JsonCreator
    public Words(@JsonProperty(WORDS_PROP) @NotNull @NotEmpty Stream<String> words) {
        this.words = words;
    }

    @JsonProperty(WORDS_PROP)
    public Stream<String> getWords() {
        return words;
    }
}
