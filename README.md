# wordcount

Wordcount is a simple stateless service which exposes the following
API:

**POST /words**

- payload: a bunch of words
- response: a histogram of word counts

example payload:

```json
{
    "words": [
        "word", "word", "word", "wat"
    ]
}
```

example response:

```json
{
    "counts": {
        "word": 3,
        "wat": 1
    }
}
```

## Why would you even do this?

I wrote this thing to help people learn about Java performance
profiling. There are great tools that are really easy to use, but in
my experience knowledge of their use isn't particularly widespread. As
a consequence, much time is wasted quibbling about whether this or
that piece of code *might* perform a certain way when one could
instead *just measure it*.

## How to start the wordcount application

1. Run `mvn clean install` to build your application.
2. Run `java -jar target/wordcount-service-0.0.1-SNAPSHOT.jar --help`
   to see what it can do.
3. Start application with `java -jar
   target/wordcount-service-0.0.1-SNAPSHOT.jar server config.yml`
4. To see that your application is running visit
   `http://localhost:8080` in your browser.
5. Fire off a request:
```
curl -XPOST -H "Content-Type: application/json" -d '{"words": ["word", "word", "word", "wat"]}' http://localhost:8080/words
```
6. Bask in the light of technology.

## Performance profiling

This section describes how to use Java Flight Recorder to profile the
performance of a running web application, and Java Mission Control to
analyze the performance metrics. We will also see how to invoke Java
Flight Recorder from the command line for use with shorter-running
Java processes.

### Prerequisites

1. Install the [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
2. Install [maven](https://maven.apache.org/).
3. Install [wrk](https://github.com/wg/wrk).

### Profiling the web application

We'll use Java Flight Recorder and Java Mission Control to collect and
analyze runtime performance data while the application is being
subjected to a load test using wrk.

1. Run `mvn clean install` to make sure you have the latest build
2. Start the application in flight recorder mode like this:
```
java -Xmx4096m -Xms4096m -XX:+UseG1GC -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -jar target/wordcount-service-0.0.1-SNAPSHOT.jar server config.yml
```
3. Start Java Mission Control by typing the command `jmc` in a second
   terminal.
4. In Java Mission Control, go to File > Connect and select the
   wordcount-service JVM from the Local folder.
5. Click Next, select Start Flight Recording, click Next again.
6. Set the Recording time to 3min, click Finish.
7. Quickly, the clock is ticking, enter the following command in a 3rd
terminal to make wrk punish the wordcount service with the complete
text of Charles Darwin's *On the Origin of Species*:
```
wrk -c 100 -t4 -d 120s --timeout 60s --latency -s perf_test.lua http://localhost:8080/words
```
8. Once your profiling run has finished, you will be able to analyze
the Flight Recording in Java Mission Control to investigate things
like GC pauses, thread contention, and hot methods.

### Profiling a short-lived Java process

Sometimes you don't have the luxury of an application which just idles
in the background, waiting for you to connect the profiler to it. Some
applications fire up a JVM, do their business, then terminate the
JVM. Profiling applications like these would seem difficult following
the process above, especially step 7. Fortunately, there's a better
way.

For your convenience, just such an application is included in the
wordcount service! To run this application, invoke the following
command:

```
time java -jar target/wordcount-service-0.0.1-SNAPSHOT.jar count -i 100 -k 1000 src/test/resources/fixtures/darwin_words.json
```

This counts up all the words in `darwin.json` 100 times in a row,
printing a JSON histogram to the console each time. Note the `time`
command. This will tell you how long the program took to execute. On
my machine, it runs in about 6 seconds.

To profile this application, we'll need to add some JVM flags to dump
a Java Flight Recorder run:

```
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true,dumponexitpath=/tmp/recording.jfr -jar target/wordcount-service-0.0.1-SNAPSHOT.jar count -i 100 -k 1000 -c hashmap src/test/resources/fixtures/darwin_words.json
```

Now you can open `/tmp/recording.jfr` in Java Mission Control and
inspect the results. For more information about Java Flight Recorder,
see
[the docs.](https://docs.oracle.com/javacomponents/jmc-5-5/jfr-runtime-guide/toc.htm)

### License issues

Don't run any JVMs in production with the
`-XX:+UnlockCommercialFeatures` flag. It's fine to run in QA or dev,
but not in prod. Oracle has a 
[hilariously evil license](http://www.oracle.com/technetwork/java/javase/terms/license/index.html) 
for JavaSE where most of it is free, but some parts (like Java Mission 
Control) are subject to commercial license if you use them in certain 
ways (like on a production system). In production, or if you are 
running OpenJDK, you can try using another profiling tool like 
[VisualVM](https://visualvm.github.io/) or 
[YourKit](https://www.yourkit.com/).

### Caveat

The technique described here uses a sampling profiler, so results may 
vary between runs, and may not accurately reflect results you would
see using a tracing profiler. Also, beware the JIT. In Java Mission 
Control you can see what the JIT was up to during your profiling run
by looking in the "Code" section. If you see lots of compilations, it's
possible your sampling run was contaminated with un-optimized code. If
you think that might be the case, try profiling for a longer run and/or
pre-warming the JIT for a period of time (i.e. running your load test) 
before starting the sampling run. In any case, these measurements are 
approximate, not absolute.
