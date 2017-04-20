package com.jgrillo.wordcount;

import com.jgrillo.wordcount.core.CounterType;

public interface Config {

    CounterType getCounterType();

    int getInitialCapacity();
}
