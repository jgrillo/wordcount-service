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
import com.jgrillo.wordcount.core.Result;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import static com.jgrillo.wordcount.WordcountTestUtil.wordsIterator;
import static org.junit.Assert.*;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;
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
                lists().of(strings().allPossible().ofLengthBetween(0, 100)).ofSizeBetween(0, 1000).describedAs(
                        Object::toString
                )
        ).asWithPrecursor(words -> {
            try {
                final byte[] bytes = writer.writeValueAsBytes(new Words(wordsIterator(words)));
                return factory.createParser(bytes);
            } catch (IOException e) {
                throw new RuntimeException("Caught IOE while serializing", e);
            }
        }).checkAssert((words, parser) -> {
            try {
                final Words wordsModel = reader.readValue(parser);

                final ImmutableList.Builder<String> deserializedWordsBuilder = ImmutableList.builder();
                try {
                    final Iterator<Result<String, IOException>> results = wordsModel.getWords();

                    while (results.hasNext()) {
                        final Result<String, IOException> result = results.next();
                        final String word = result.get();

                        if (word != null) {
                            deserializedWordsBuilder.add(word);
                        }
                    }
                } catch (IOException e) {
                    fail(e.getMessage());
                }

                assertThat(words).isEqualTo(deserializedWordsBuilder.build());
            } catch (IOException e) {
                throw new RuntimeException("Caught IOE while deserializing", e);
            }
        });
    }

    @Test
    public void testWordsSerializesToJSON() throws Exception {
        final Words words = new Words(
                ImmutableList.<Result<String, IOException>>builder()
                        .add(() -> "word")
                        .add(() -> "word")
                        .add(() -> "word")
                        .add(() -> "wat")
                        .build()
                        .iterator()
        );

        final byte[] bytes = fixture("fixtures/words.json").getBytes(Charsets.UTF_8);
        try (final JsonParser parser = factory.createParser(bytes)) {

            final byte[] expected = writer.writeValueAsBytes(reader.readValue(parser)); // normalized

            assertThat(writer.writeValueAsBytes(words)).isEqualTo(expected);
        }
    }

    @Test
    public void testWordsDeserializesFromJSON() throws Exception {
        final byte[] bytes = fixture("fixtures/words.json").getBytes(Charsets.UTF_8);
        final ImmutableList.Builder<String> deserializedWordsBuilder = ImmutableList.builder();

        final Words wordsModel;
        try (final JsonParser parser = factory.createParser(bytes)) {
            wordsModel = reader.readValue(parser);

            final Iterator<Result<String, IOException>> results = wordsModel.getWords();
            try {
                while (results.hasNext()) {
                    final Result<String, IOException> result = results.next();
                    final String word = result.get();

                    if (word != null) {
                        deserializedWordsBuilder.add(word);
                    }
                }
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        final Words expectedWords = new Words(
                ImmutableList.<Result<String, IOException>>builder()
                        .add(() -> "word")
                        .add(() -> "word")
                        .add(() -> "word")
                        .add(() -> "wat")
                        .build()
                        .iterator()
        );
        final Iterator<Result<String, IOException>> expectedResults = expectedWords.getWords();
        final ImmutableList.Builder<String> expectedWordsBuilder = ImmutableList.builder();
        try {
            while (expectedResults.hasNext()) {
                final Result<String, IOException> expectedResult = expectedResults.next();
                final String word = expectedResult.get();

                if (word != null) {
                    expectedWordsBuilder.add(word);
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertThat(deserializedWordsBuilder.build()).isEqualTo(expectedWordsBuilder.build());
    }
}