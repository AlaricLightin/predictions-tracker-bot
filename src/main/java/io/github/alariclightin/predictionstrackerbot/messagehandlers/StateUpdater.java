package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;

@FunctionalInterface
public interface StateUpdater<T> {
    T apply(String text, T data) throws UnexpectedUserMessageException;    
}
