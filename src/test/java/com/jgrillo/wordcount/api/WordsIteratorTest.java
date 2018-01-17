package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.jgrillo.wordcount.core.Result;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public final class WordsIteratorTest {
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.CLOSE_CLOSEABLE)
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    private static final JsonFactory factory = mapper.getFactory();

    @Test
    public void hasNext_returnsTrue_initially() throws Exception {
        final JsonParser jsonParser = factory.createParser("{\"wat\": 666}".getBytes(Charsets.UTF_8));
        final WordsIterator wordsIterator = new WordsIterator(jsonParser);

        assertTrue(wordsIterator.hasNext());
    }

    @Test
    public void next_returnsWordsProcessingExceptionResult_whenFieldNameIncorrect() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"wat\": 666}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                final Result<String, IOException> result = wordsIterator.next();
                result.get();
            } catch (Exception e) {
                assertTrue(e instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown a WordsProcessingException");
        }
    }

    @Test
    public void next_returnsWordsProcessingExceptionResult_whenWordsFieldIsNotArray() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\": 666}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                final Result<String, IOException> result = wordsIterator.next();
                result.get();
            } catch (IOException e) {
                assertTrue(e instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown a WordsProcessingException");
        }
    }

    @Test
    public void next_returnsWordsProcessingExceptionResult_whenWordsArrayContainsNumber() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[666]}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                final Result<String, IOException> result = wordsIterator.next();
                result.get();
            } catch (IOException e) {
                assertTrue(e instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown a WordsProcessingException");
        }
    }

    @Test
    public void next_returnsWordsProcessingExceptionResult_whenWordsArrayContainsValidWordsObject() throws Exception {
        try (final JsonParser jsonParser = factory.createParser(
                "{\"words\":[{\"words\":[\"word\"]}]}".getBytes(Charsets.UTF_8)
        )) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                final Result<String, IOException> result = wordsIterator.next();
                result.get();
            } catch (IOException e) {
                assertTrue(e instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown a WordsProcessingException");
        }
    }

    @Test
    public void next_returnsWordsProcessingExceptionResult_whenWordsArrayContainsArray() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[[\"word\"]]}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                final Result<String, IOException> result = wordsIterator.next();
                result.get();
            } catch (IOException e) {
                assertTrue(e instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown a WordsProcessingException");
        }
    }

    @Test
    public void next_throwsNoSuchElementException_whenWordsArrayExhausted() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[]}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                assertTrue(wordsIterator.hasNext());
                assertNull(wordsIterator.next().get());
                assertFalse(wordsIterator.hasNext());
                wordsIterator.next().get();
            } catch (Exception e) {
                assertTrue(e instanceof NoSuchElementException);
                assertFalse(wordsIterator.hasNext());
                return;
            }

            fail("Should have thrown NoSuchElementException");
        }
    }

    @Test
    public void next_returnsOneWord_whenWordsArrayContainsOneWord() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[\"word\"]}".getBytes(Charsets.UTF_8))) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);
            final Result<String, IOException> result = wordsIterator.next();
            final String word = result.get();

            if (word == null) {
                System.out.println("null");
            } else {
                System.out.println(word);
            }

            assertFalse(wordsIterator.hasNext());
            assertEquals("word", word);
        }
    }

    @Test
    public void next_returnsTwoWords_whenWordsArrayContainsTwoWords() throws Exception {
        try (final JsonParser jsonParser = factory.createParser(
                "{\"words\":[\"word1\",\"word2\"]}".getBytes(Charsets.UTF_8)
        )) {
            final WordsIterator wordsIterator = new WordsIterator(jsonParser);
            final Result<String, IOException> result1 = wordsIterator.next();
            final String word1 = result1.get();

            assertEquals("word1", word1);
            assertTrue(wordsIterator.hasNext());

            final Result<String, IOException> result2 = wordsIterator.next();
            final String word2 = result2.get();

            assertFalse(wordsIterator.hasNext());
            assertEquals("word2", word2);
        }
    }
}