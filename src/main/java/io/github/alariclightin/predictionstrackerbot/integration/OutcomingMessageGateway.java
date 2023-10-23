package io.github.alariclightin.predictionstrackerbot.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@MessagingGateway
public interface OutcomingMessageGateway {
        
    @Gateway(requestChannel = "outcomingMessagesChannel")
    void sendMessage(SendMessage sendMessage);
}
