package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.CommandInlineButton;

enum LanguageAction implements CommandInlineButton {
    ENGLISH("bot.buttons.setlanguage.english", "en"),
    RUSSIAN("bot.buttons.setlanguage.russian", "ru");
    
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
        return SetLanguageConsts.COMMAND;
    }

    @Override
    public String phase() {
        return SetLanguageConsts.DATA_INPUT_PHASE;
    }

    @Override
    public String buttonId() {
        return this.toString();
    }

    String languageCode() {
        return languageCode;
    }
}
