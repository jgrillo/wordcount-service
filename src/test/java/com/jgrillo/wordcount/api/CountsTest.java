package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;
import static io.dropwizard.testing.FixtureHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class CountsTest {
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.CLOSE_CLOSEABLE)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    private static final ObjectWriter writer = mapper.writerFor(Counts.class);
    private static final ObjectReader reader = mapper.readerFor(Counts.class);
    private static final JsonFactory factory = mapper.getFactory();

    /**
     * Test the encode-decode invariant for the Counts model.
     */
    @Test
    public void testCountsEncodeDecode() throws Exception {
        qt().forAll(
                lists().of(strings().allPossible().ofLengthBetween(0, 100)).ofSize(1000).describedAs(
                        Object::toString
                ),
                lists().of(longs().all()).ofSize(1000).describedAs(Object::toString)
        ).as((words, counts) -> {
            final ImmutableMap.Builder<String, Long> mapBuilder = ImmutableMap.builder();
            final List<String> distinctWords = words.stream().distinct().collect(Collectors.toList());

            for (int i = 0; i < distinctWords.size(); i++) {
                mapBuilder.put(distinctWords.get(i), counts.get(i)); // counts.size() >= distinctWords.size()
            }

            return mapBuilder.build();
        }).checkAssert((wordCounts) -> {
            try {
                final byte[] bytes = writer.writeValueAsBytes(new Counts(wordCounts));
                final JsonParser parser = factory.createParser(bytes);
                final Counts countsModel = reader.readValue(parser);

                assertThat(countsModel.getCounts()).isEqualTo(wordCounts);
            } catch (IOException e) {
                throw new RuntimeException("Caught IOE while checking counts", e);
            }
        });
    }

    @Test
    public void testCountsSerializesToJSON() throws Exception {
        final Counts counts = new Counts(
                ImmutableMap.<String, Long>builder()
                        .put("word", 3L)
                        .put("wat", 1L)
                        .build()
        );

        final String expected = writer.writeValueAsString(reader.readValue(fixture("fixtures/counts.json")));

        assertThat(writer.writeValueAsString(counts)).isEqualTo(expected);
    }

    @Test
    public void testCountsDeserializesFromJSON() throws Exception {
        final Counts counts = new Counts(
                ImmutableMap.<String, Long>builder()
                        .put("word", 3L)
                        .put("wat", 1L)
                        .build()
        );

        final Counts deserializedCounts = reader.readValue(fixture("fixtures/counts.json"));

        assertThat(deserializedCounts.getCounts()).isEqualTo(counts.getCounts());
    }
}