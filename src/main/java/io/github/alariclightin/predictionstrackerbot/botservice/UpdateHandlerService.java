package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class UpdateHandlerService {
    private final MessageHandlingService messageHandlingService;
    private final SendMessageService sendMessageService;
    private final OutcomingMessageGateway outcomingMessageGateway;

    UpdateHandlerService(
        MessageHandlingService messageHandlingService,
        SendMessageService sendMessageService,
        OutcomingMessageGateway outcomingMessageGateway) {
            
        this.messageHandlingService = messageHandlingService;
        this.sendMessageService = sendMessageService;
        this.outcomingMessageGateway = outcomingMessageGateway;
    }

    @ServiceActivator(inputChannel = "incomingUpdatesChannel", outputChannel = "outcomingMessagesChannel")
    public void handleUpdate(Update update) {
        Message incomingMessage = update.getMessage();
        if (incomingMessage == null) {
            return;
        }

        BotMessage botMessage = messageHandlingService.handleTextMessage(
            new UserTextMessage(incomingMessage));
        SendMessage sendMessage = sendMessageService.create(
            incomingMessage.getChatId(), 
            incomingMessage.getFrom().getLanguageCode(), 
            botMessage
        );

        outcomingMessageGateway.sendMessage(sendMessage);
    }
    
}
