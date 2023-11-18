package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.SimpleInputCommandBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.CommandInlineButton;

enum LanguageAction implements CommandInlineButton {
    ENGLISH("bot.buttons.setlanguage.english", "en"),
    RUSSIAN("bot.buttons.setlanguage.russian", "ru");
    
    static final String COMMAND_NAME = "setlanguage";

    private final String messageId;
    private final String languageCode;

    private LanguageAction(String messageId, String languageCode) {
        this.messageId = messageId;
        this.languageCode = languageCode;
    }

    @Override
    public String messageId() {
        return messageId;
    }

    @Override
    public String command() {
        return COMMAND_NAME;
    }

    @Override
    public String phase() {
        return SimpleInputCommandBuilder.INPUT_PHASE;
    }

    @Override
    public String buttonId() {
        return this.toString();
    }

    String languageCode() {
        return languageCode;
    }
}
