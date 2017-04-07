package com.jgrillo.wordcount;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.cli.CountCommand;
import com.jgrillo.wordcount.resources.WordsResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public final class WordcountApplication extends Application<WordcountConfiguration> {

    public static void main(final String[] args) throws Exception {
        new WordcountApplication().run(args);
    }

    @Override
    public String getName() {
        return "wordcount";
    }

    @Override
    public void initialize(final Bootstrap<WordcountConfiguration> bootstrap) {
        bootstrap.setObjectMapper(
                bootstrap.getObjectMapper()
                        .disable(SerializationFeature.CLOSE_CLOSEABLE)
                        .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
        );
        bootstrap.addCommand(new CountCommand());
    }

    @Override
    public void run(final WordcountConfiguration configuration, final Environment environment) {
        final ObjectWriter writer = environment.getObjectMapper().writerFor(Counts.class);
        final ObjectReader reader = environment.getObjectMapper().readerFor(Words.class);

        environment.jersey().register(new WordsResource(configuration, writer, reader));
    }
}
