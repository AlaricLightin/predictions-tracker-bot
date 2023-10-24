package io.github.alariclightin.predictionstrackerbot.schedulers;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@MessagingGateway
interface ReminderGateway {

    @Gateway(requestChannel = "botMessageChannel")
    void sendBotMessage(@Header("chatId") long chatId, @Payload BotMessage message);
    
}
