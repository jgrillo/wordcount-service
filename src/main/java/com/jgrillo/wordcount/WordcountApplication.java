package com.jgrillo.wordcount;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.jgrillo.wordcount.api.Counts;
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
        bootstrap.addCommand(new CountCommand());
    }

    @Override
    public void run(final WordcountConfiguration configuration, final Environment environment) {
        final ObjectWriter writer = environment.getObjectMapper().writer().forType(Counts.class);

        environment.jersey().register(new WordsResource(configuration, writer));
    }
}
