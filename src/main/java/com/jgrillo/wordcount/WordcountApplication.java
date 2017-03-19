package com.jgrillo.wordcount;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WordcountApplication extends Application<WordcountConfiguration> {

    public static void main(final String[] args) throws Exception {
        new WordcountApplication().run(args);
    }

    @Override
    public String getName() {
        return "wordcount";
    }

    @Override
    public void initialize(final Bootstrap<WordcountConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final WordcountConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
