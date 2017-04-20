package com.jgrillo.wordcount.resources;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.jgrillo.wordcount.Config;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.core.CounterType;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static io.dropwizard.testing.FixtureHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.*;

public final class WordsResourceTest {
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.CLOSE_CLOSEABLE)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    private static final ObjectWriter countsWriter = mapper.writerFor(Counts.class);
    private static final ObjectReader countsReader = mapper.readerFor(Counts.class);
    private static final ObjectReader wordsReader = mapper.readerFor(Words.class);
    private static final JsonFactory factory = mapper.getFactory();
    private static final Config config = mock(Config.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new WordsResource(config, countsWriter, wordsReader))
            .build();

    @Test
    public synchronized void countWords() throws Exception {
        try {
            qt().withExamples(200).withShrinkCycles(20).forAll(
                    arbitrary().enumValues(CounterType.class),
                    integers().between(0, 100),
                    booleans().all()
            ).checkAssert((counterType, initialCapacity, parallel) -> {
                when(config.getCounterType()).thenReturn(counterType);
                when(config.getInitialCapacity()).thenReturn(initialCapacity);

                final byte[] wordsBytes = fixture("fixtures/darwin_words.json").getBytes(Charsets.UTF_8);

                final Response response = resources.client().target("/words").request()
                        .buildPost(Entity.json(wordsBytes))
                        .invoke();

                assertEquals(200, response.getStatus());

                final byte[] countsBytes = fixture("fixtures/darwin_counts.json").getBytes(Charsets.UTF_8);
                final InputStream responseStream = (InputStream) response.getEntity();
                try (
                        final JsonParser expectedParser = factory.createParser(countsBytes);
                        final JsonParser responseParser = factory.createParser(responseStream);
                ) {
                    final Counts expectedCounts = countsReader.readValue(expectedParser);
                    final Counts counts = countsReader.readValue(responseParser);

                    assertThat(counts.getCounts()).isEqualTo(expectedCounts.getCounts());
                } catch (IOException e) {
                    e.printStackTrace();
                    fail();
                }

                verify(config, atLeastOnce()).getCounterType();
                verify(config, atLeastOnce()).getInitialCapacity();
            });
        } finally {
            reset(config);
        }
    }
}