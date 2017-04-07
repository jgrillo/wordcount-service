package com.jgrillo.wordcount.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

public final class CountsSerializer extends StdSerializer<Counts> {

    public CountsSerializer() {
        this(Counts.class);
    }

    private CountsSerializer(Class<Counts> t) {
        super(t);
    }

    @Override
    public void serialize(
            Counts counts, JsonGenerator jsonGenerator, SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectFieldStart(Counts.COUNTS_PROP);

        for (Map.Entry<String, Long> entry: counts.getCounts().entrySet()) {
            jsonGenerator.writeNumberField(entry.getKey(), entry.getValue());
        }

        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
