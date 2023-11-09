package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.CommandInlineButton;

enum ResultUserAction implements CommandInlineButton {
    YES("bot.buttons.yes"), 
    NO("bot.buttons.no"), 
    SKIP("bot.buttons.skip"), 
    SKIP_ALL("bot.buttons.skip-all");

    private final String messageId;

    private ResultUserAction(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String messageId() {
        return messageId;
    }

    @Override
    public String command() {
        return AbstractSetResultsHandler.COMMAND;
    }

    @Override
    public String phase() {
        return AbstractSetResultsHandler.SET_RESULT_PHASE;
    }

    @Override
    public String buttonId() {
        return this.toString();
    }
}
