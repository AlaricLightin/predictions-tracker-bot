package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;

@FunctionalInterface
public interface ResultAction<T> {
    void apply(UserMessage message, T data);
}
