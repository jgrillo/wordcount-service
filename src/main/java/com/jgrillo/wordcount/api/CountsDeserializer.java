package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;

public final class CountsDeserializer extends StdDeserializer<Counts> {

    public CountsDeserializer() {
        this(Counts.class);
    }

    private CountsDeserializer(Class<Counts> t) {
        super(t);
    }

    @Override
    public Counts deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext
    ) throws IOException {
        final ImmutableMap.Builder<String, Long> mapBuilder = ImmutableMap.builder();

        JsonToken token;
        boolean started = false;
        boolean stopped = false;
        while ((token = jsonParser.nextToken()) != null) {
            if (stopped) {
                break;
            }

            switch (token) {
                case START_OBJECT:
                    break;
                case FIELD_NAME:
                    if (started) {
                        break;
                    }

                    final String name = jsonParser.getCurrentName();
                    if (name.equals(Counts.COUNTS_PROP)) {
                        started = true;
                        break;
                    } else {
                        throw new CountsProcessingException(
                                String.format("Encountered unknown field: \"%s\"", name),
                                jsonParser.getCurrentLocation()
                        );
                    }
                case VALUE_NUMBER_INT:
                    if (started) {
                        mapBuilder.put(jsonParser.getCurrentName(), jsonParser.getLongValue());
                    }
                    break;
                case END_OBJECT:
                    stopped = true;
                    break;
                default:
                    throw new CountsProcessingException("Encountered unknown state", jsonParser.getCurrentLocation());
            }
        }

        return new Counts(mapBuilder.build());
    }
}
