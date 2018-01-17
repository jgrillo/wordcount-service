package com.jgrillo.wordcount.core;

@FunctionalInterface
public interface Result<T, E extends Exception> {
    T get() throws E;
}
