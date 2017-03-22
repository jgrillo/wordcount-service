package com.jgrillo.wordcount.core;

public class CounterFactory {
    public static Counter newCounter(CounterType type, Integer initialCapacity) {
        switch (type) {
            case HASHMAP:
                return new HashMapCounter(initialCapacity);
            case MULTISET:
                return new MultiSetCounter(initialCapacity);
            case STREAM:
                return new StreamCounter(initialCapacity);
            default:
                throw new UnsupportedOperationException(String.format("Unknown CounterType: %s", type));
        }
    }
}
