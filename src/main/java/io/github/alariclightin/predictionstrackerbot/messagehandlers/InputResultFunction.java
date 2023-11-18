package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;

@FunctionalInterface
public interface InputResultFunction<T> {
    T apply(String text) throws UnexpectedUserMessageException;
}
