package com.jgrillo.wordcount.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jgrillo.wordcount.Config;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.core.Counter;
import com.jgrillo.wordcount.core.CounterFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;

@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WordsResource {
    private final Config config;
    private final ObjectWriter countsWriter;
    private final ObjectReader wordsReader;

    public WordsResource(Config config, ObjectWriter countsWriter, ObjectReader wordsReader) {
        this.config = config;
        this.countsWriter = countsWriter;
        this.wordsReader = wordsReader;
    }

    @POST
    @Timed(name = "words-timed")
    @Metered(name = "words-metered")
    @ExceptionMetered(name = "words-exception-metered")
    public Response countWords(InputStream inputStream) throws IOException {
        final Counter counter = CounterFactory.newCounter(config.getCounterType(), config.getInitialCapacity());

        try (final JsonParser jsonParser = wordsReader.getFactory().createParser(inputStream)) {
            final Words words = wordsReader.readValue(jsonParser);
            final Counts counts = new Counts(counter.getCounts(words.getWords()));

            return  Response.ok((StreamingOutput) outputStream -> {
                final JsonGenerator jsonGenerator = countsWriter.getFactory().createGenerator(outputStream);
                countsWriter.writeValue(jsonGenerator, counts);
            }).build();
        }
    }
}
