package com.jgrillo.wordcount.core;

public final class CounterFactory {
    public static Counter newCounter(CounterType type, Integer initialCapacity) {
        switch (type) {
            case HASHMAP:
                return new HashMapCounter(initialCapacity);
            case CONCURRENT:
                return new ConcurrentHashMapCounter(initialCapacity);
            case STREAM:
                return new StreamCounter();
            case FAST:
                return new FastCounter(initialCapacity);
            default:
                throw new UnsupportedOperationException(String.format("Unknown CounterType: %s", type));
        }
    }
}
