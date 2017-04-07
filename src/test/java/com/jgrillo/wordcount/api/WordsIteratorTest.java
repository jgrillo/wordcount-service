package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import org.junit.Test;

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
    public void next_throwsWrappedWordsProcessingException_whenFieldNameIncorrect() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"wat\": 666}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                wordsIterator.next();
            } catch (WordsIOEWrapper e) {
                assertTrue(e.getCause() instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown WordsIOEWrapper containing a WordsProcessingException");
        }
    }

    @Test
    public void next_throwsWrappedWordsProcessingException_whenWordsFieldIsNotArray() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\": 666}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                wordsIterator.next();
            } catch (WordsIOEWrapper e) {
                assertTrue(e.getCause() instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown WordsIOEWrapper containing a WordsProcessingException");
        }
    }

    @Test
    public void next_throwsWrappedWordsProcessingException_whenWordsArrayContainsNumber() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[666]}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                wordsIterator.next();
            } catch (WordsIOEWrapper e) {
                assertTrue(e.getCause() instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown WordsIOEWrapper containing a WordsProcessingException");
        }
    }

    @Test
    public void next_throwsWrappedWordsProcessingException_whenWordsArrayContainsValidWordsObject() throws Exception {
        try (final JsonParser jsonParser = factory.createParser(
                "{\"words\":[{\"words\":[\"word\"]}]}".getBytes(Charsets.UTF_8)
        )) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                wordsIterator.next();
            } catch (WordsIOEWrapper e) {
                assertTrue(e.getCause() instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown WordsIOEWrapper containing a WordsProcessingException");
        }
    }

    @Test
    public void next_throwsWrappedWordsProcessingException_whenWordsArrayContainsArray() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[[\"word\"]]}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);

            try {
                wordsIterator.next();
            } catch (WordsIOEWrapper e) {
                assertTrue(e.getCause() instanceof WordsProcessingException);
                return;
            }

            fail("Should have thrown WordsIOEWrapper containing a WordsProcessingException");
        }
    }

    @Test
    public void next_returnsNull_whenWordsArrayEmpty() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[]}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);
            final String word = wordsIterator.next();

            assertNull(word);
            assertFalse(wordsIterator.hasNext());
        }
    }

    @Test
    public void next_returnsOneWord_whenWordsArrayContainsOneWord() throws Exception {
        try (final JsonParser jsonParser = factory.createParser("{\"words\":[\"word\"]}".getBytes(Charsets.UTF_8))) {

            final WordsIterator wordsIterator = new WordsIterator(jsonParser);
            final String word = wordsIterator.next();

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
            final String word1 = wordsIterator.next();

            assertEquals("word1", word1);
            assertTrue(wordsIterator.hasNext());

            final String word2 = wordsIterator.next();

            assertFalse(wordsIterator.hasNext());
            assertEquals("word2", word2);
        }
    }
}