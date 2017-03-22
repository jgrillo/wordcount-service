package com.jgrillo.wordcount.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.core.Counter;
import com.jgrillo.wordcount.core.CounterFactory;
import com.jgrillo.wordcount.core.CounterType;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WordsResource {
    private final CounterType counterType;
    private final Integer initialCapacity;

    public WordsResource(CounterType counterType, Integer initialCapacity) {
        this.counterType = counterType;
        this.initialCapacity = initialCapacity;
    }

    @POST
    @Timed(name = "words-timed")
    @Metered(name = "words-metered")
    @ExceptionMetered(name = "words-exception-metered")
    public Counts countWords(@Valid Words words) {
        final Counter counter = CounterFactory.newCounter(counterType, initialCapacity);

        counter.putAll(words.getWords());
        Map<String, Long> wordCounts = counter.getCounts();

        return new Counts(wordCounts);
    }
}
