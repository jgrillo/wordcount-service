package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jgrillo.wordcount.core.Result;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Iterator;

@JsonDeserialize(using = WordsDeserializer.class)
@JsonSerialize(using = WordsSerializer.class)
public final class Words {
    public static final String WORDS_PROP = "words";
    private final Iterator<Result<String, IOException>> words;

    @JsonCreator
    public Words(@JsonProperty(WORDS_PROP) @NotNull @NotEmpty Iterator<Result<String, IOException>> words) {
        this.words = words;
    }

    @JsonProperty(WORDS_PROP)
    public Iterator<Result<String, IOException>> getWords() {
        return words;
    }
}
