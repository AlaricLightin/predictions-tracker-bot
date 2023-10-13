package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

@FunctionalInterface
public interface ResultAction<T> {
    void apply(Message message, T data);
}
