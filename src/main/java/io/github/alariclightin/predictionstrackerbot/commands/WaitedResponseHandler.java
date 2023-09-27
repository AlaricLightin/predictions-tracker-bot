package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface WaitedResponseHandler {
    SendMessage handleWaitedResponse(Message message);
    
    String getCommandName();
}
