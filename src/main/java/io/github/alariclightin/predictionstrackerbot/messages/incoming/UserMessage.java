package io.github.alariclightin.predictionstrackerbot.messages.incoming;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.User;

public interface UserMessage {
    User getUser();
    String getText();
    Instant getDateTime();
    long getChatId();
}
