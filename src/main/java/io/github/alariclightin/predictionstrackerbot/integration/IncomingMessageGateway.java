package io.github.alariclightin.predictionstrackerbot.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.telegram.telegrambots.meta.api.objects.Update;

@MessagingGateway
public interface IncomingMessageGateway {
    
    @Gateway(requestChannel = "incomingUpdatesChannel")
    void handleUpdate(Update update);
}
