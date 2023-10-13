package io.github.alariclightin.predictionstrackerbot.commands;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;

@FunctionalInterface
public interface StateUpdater<T> {
    T apply(String text, T data) throws UnexpectedMessageException;    
}
