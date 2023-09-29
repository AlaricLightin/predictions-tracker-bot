package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;

public interface WaitedResponseHandler {
    BotMessage handleWaitedResponse(Message message);
    
    String getCommandName();
}
