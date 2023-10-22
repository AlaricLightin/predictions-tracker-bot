package io.github.alariclightin.predictionstrackerbot.commands;

import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;

@FunctionalInterface
public interface ResultAction<T> {
    void apply(UserMessage message, T data);
}
