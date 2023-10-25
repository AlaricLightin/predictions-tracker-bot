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

    public static boolean isButtonCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data == null) return 
            false;

        String[] dataParts = data.split(ButtonsConsts.ID_DELIMITER);
        return dataParts.length >= 4 && dataParts[0].equals(ButtonsConsts.BUTTON_PREFIX);
    }

    public ButtonCallbackQuery(CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;

        if (callbackQuery.getData() == null) {
            throw new IllegalArgumentException("Callback query data is null");
        }
        
        String[] data = callbackQuery.getData().split(ButtonsConsts.ID_DELIMITER);
        if (data.length < 4)
            throw new IllegalArgumentException("Callback query data is invalid");

        this.command = data[1];
        this.phase = data[2];
        this.buttonId = data[3];
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

    public String getCommand() {
        return command;
    }
    
    public String getPhase() {
        return phase;
    }
}
