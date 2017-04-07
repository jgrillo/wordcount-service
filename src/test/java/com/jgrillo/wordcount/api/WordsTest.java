package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.*;
import static io.dropwizard.testing.FixtureHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class WordsTest {
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.CLOSE_CLOSEABLE)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    private static final JsonFactory factory = mapper.getFactory();
    private static final ObjectReader reader = mapper.readerFor(Words.class);
    private static final ObjectWriter writer = mapper.writerFor(Words.class);

    /**
     * Test the encode-decode invariant for the Words model.
     */
    @Test
    public void testWordsEncodeDecode() throws Exception {
        qt().forAll(
                lists().allListsOf(strings().allPossible().ofLengthBetween(0, 100)).ofSizeBetween(0, 1000).describedAs(
                        Object::toString
                )
        ).asWithPrecursor(words -> {
            try {
                final byte[] bytes = writer.writeValueAsBytes(new Words(words.stream()));
                return factory.createParser(bytes);
            } catch (IOException e) {
                throw new RuntimeException("Caught IOE while serializing", e);
            }
        }).checkAssert((words, parser) -> {
            try {
                final Words wordsModel = reader.readValue(parser);

                final List<String> deserializedWords = wordsModel.getWords().collect(Collectors.toList());

                assertThat(words).isEqualTo(deserializedWords);
            } catch (IOException e) {
                throw new RuntimeException("Caught IOE while deserializing", e);
            }
        });
    }

    @Test
    public void testWordsSerializesToJSON() throws Exception {
        final Words words = new Words(
                Stream.<String>builder()
                        .add("word")
                        .add("word")
                        .add("word")
                        .add("wat")
                        .build()
        );

        final byte[] bytes = fixture("fixtures/words.json").getBytes(Charsets.UTF_8);
        try (final JsonParser parser = factory.createParser(bytes)) {

            final byte[] expected = writer.writeValueAsBytes(reader.readValue(parser)); // normalized

            assertThat(writer.writeValueAsBytes(words)).isEqualTo(expected);
        }
    }

    @Test
    public void testWordsDeserializesFromJSON() throws Exception {
        final Words words = new Words(
                Stream.<String>builder()
                        .add("word")
                        .add("word")
                        .add("word")
                        .add("wat")
                        .build()
        );

        final byte[] bytes = fixture("fixtures/words.json").getBytes(Charsets.UTF_8);
        try (final JsonParser parser = factory.createParser(bytes)) {
            final Words deserializedWordsModel = reader.readValue(parser);
            final Iterator<String> deserializedWordsIterator = deserializedWordsModel.getWords().sorted().iterator();

            final ImmutableList.Builder<String> deserializedWordsListBuilder = ImmutableList.builder();
            while (deserializedWordsIterator.hasNext()) {
                final String word = deserializedWordsIterator.next();
                deserializedWordsListBuilder.add(word);
            }

            final List<String> deserializedWords = deserializedWordsListBuilder.build();

            assertThat(deserializedWords).isEqualTo(words.getWords().sorted().collect(Collectors.toList()));
        }
    }
}