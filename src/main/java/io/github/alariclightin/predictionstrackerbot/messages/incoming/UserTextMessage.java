package io.github.alariclightin.predictionstrackerbot.messages.incoming;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserTextMessage implements UserMessage {
    private final Message message;

    public UserTextMessage(Message message) {
        this.message = message;
    }

    @Override
    public User getUser() {
        return message.getFrom();
    }

    @Override
    public String getText() {
        return message.getText();
    }

    @Override
    public Instant getDateTime() {
        return Instant.ofEpochSecond(message.getDate());
    }

    public boolean isCommand() {
        return message.isCommand();
    }

}
