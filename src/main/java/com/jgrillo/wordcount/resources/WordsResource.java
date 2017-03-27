package com.jgrillo.wordcount.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jgrillo.wordcount.WordcountConfiguration;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.core.Counter;
import com.jgrillo.wordcount.core.CounterFactory;
import com.jgrillo.wordcount.core.CounterType;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WordsResource {
    private final CounterType counterType;
    private final Integer initialCapacity;
    private final boolean parallel;
    private final ObjectWriter writer;

    public WordsResource(WordcountConfiguration configuration, ObjectWriter writer) {
        this.counterType = configuration.getCounterType();
        this.initialCapacity = configuration.getInitialCapacity();
        this.parallel = configuration.getParallel();
        this.writer = writer;
    }

    @POST
    @Timed(name = "words-timed")
    @Metered(name = "words-metered")
    @ExceptionMetered(name = "words-exception-metered")
    public StreamingOutput countWords(@Valid Words words) {
        final Counter counter = CounterFactory.newCounter(counterType, initialCapacity);
        final Stream<String> wordsStream = StreamSupport.stream(words.getWords().spliterator(), parallel);

        return outputStream -> {
            final Counts counts = new Counts(counter.getCounts(wordsStream));
            final JsonGenerator jsonGenerator = writer.getFactory().createGenerator(outputStream);

            writer.writeValue(jsonGenerator, counts);
        };
    }
}
