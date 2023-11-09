package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.CommandInlineButton;

public enum DateTimeAction implements CommandInlineButton {
    ONE_MINUTE("bot.buttons.one-minute"), 
    ONE_HOUR("bot.buttons.one-hour"), 
    TODAY("bot.buttons.today"), 
    TOMORROW("bot.buttons.tomorrow"), 
    NEXT_MONTH("bot.buttons.next-month"), 
    NEXT_YEAR("bot.buttons.next-year");

    private final String messageId;

    private DateTimeAction(String messageId) {
        this.messageId = messageId;
    } 

    @Override
    public String messageId() {
        return messageId;
    }

    @Override
    public String command() {
        return AddPredictionConsts.COMMAND_NAME;
    }

    @Override
    public String phase() {
        return AddPredictionConsts.DATE_PHASE;
    }

    @Override
    public String buttonId() {
        return this.toString();
    }
}
