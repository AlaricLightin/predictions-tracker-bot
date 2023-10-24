package io.github.alariclightin.predictionstrackerbot.messages.incoming;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.messages.ButtonsConsts;

public class ButtonCallbackQuery implements UserMessage {
    private final CallbackQuery callbackQuery;
    private final String command;
    private final String phase;
    private final String buttonId;

    public ButtonCallbackQuery(CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;

        if (callbackQuery.getData() == null) {
            throw new IllegalArgumentException("Callback query data is null");
        }
        
        String[] data = callbackQuery.getData().split(ButtonsConsts.ID_DELIMITER);
        if (data.length < 3)
            throw new IllegalArgumentException("Callback query data is invalid");

        this.command = data[0];
        this.phase = data[1];
        this.buttonId = data[2];
    }

    public String getId() {
        return callbackQuery.getId();
    }

    @Override
    public User getUser() {
        return callbackQuery.getFrom();
    }

    @Override
    public String getText() {
        return buttonId;
    }

    // TODO refactor
    @Override
    public Instant getDateTime() {
        return null;
    }

    @Override
    // Now we working only with users privately
    public long getChatId() {
        return callbackQuery.getFrom().getId();
    }

    public String getCommand() {
        return command;
    }
    
    public String getPhase() {
        return phase;
    }
}
