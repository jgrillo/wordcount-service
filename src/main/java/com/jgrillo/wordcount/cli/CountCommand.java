package com.jgrillo.wordcount.cli;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jgrillo.wordcount.api.Counts;
import com.jgrillo.wordcount.api.Words;
import com.jgrillo.wordcount.core.Counter;
import com.jgrillo.wordcount.core.CounterFactory;
import com.jgrillo.wordcount.core.CounterType;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.*;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class CountCommand extends Command {
    private static final String ITERATIONS = "iterations";
    private static final String CAPACITY = "capacity";
    private static final String FILE = "file";
    private static final String COUNTER = "counter";

    public CountCommand() {
        super("count", "Count up all the words in the JSON input file.");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-i")
                .dest(ITERATIONS)
                .type(Integer.class)
                .required(true)
                .help("The number of times to read the input file.");

        subparser.addArgument("-k")
                .dest(CAPACITY)
                .type(Integer.class)
                .required(true)
                .help("The initial capacity of the counter.");

        subparser.addArgument("-c")
                .dest(COUNTER)
                .choices(CounterType.values())
                .type((argumentParser, argument, value) -> CounterType.valueOf(value.toUpperCase()))
                .required(true)
                .help("The counter type to use.");

        subparser.addArgument(FILE)
                .dest(FILE)
                .type((parser, arg, value) -> {
                    final File file = new File(value);

                    if (!Files.isRegularFile(file.toPath())) {
                        throw new ArgumentParserException(String.format("%s is not a file.", value), parser);
                    }

                    return file;
                })
                .required(true)
                .help("The JSON input file.");
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        final Integer iterations = namespace.getInt(ITERATIONS);
        final CounterType counterType = (CounterType) namespace.getAttrs().get(COUNTER);
        final Integer capacity = namespace.getInt(CAPACITY);
        final String inputPath = ((File) namespace.getAttrs().get(FILE)).getAbsolutePath();

        final ObjectMapper mapper = bootstrap.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        final ObjectReader reader = mapper.reader().forType(Words.class);
        final ObjectWriter writer = mapper.writer().forType(Counts.class);

        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        for (int i = 0; i < iterations; i++) {
            final Counter counter = CounterFactory.newCounter(counterType, capacity);

            final InputStream inputStream = new FileInputStream(new File(inputPath));

            final JsonParser jsonParser = reader.getFactory().createParser(inputStream);
            final Words words = reader.readValue(jsonParser);

            executorService.submit(() -> {
                try {
                    System.out.println(writer.writeValueAsString(new Counts(counter.getCounts(words.getWords()))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();
    }
}
