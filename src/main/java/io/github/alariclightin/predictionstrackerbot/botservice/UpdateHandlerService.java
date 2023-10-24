package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    @ServiceActivator(inputChannel = "incomingUserMessageChannel")
    public void handleTextMessage(UserTextMessage userTextMessage) {
        BotMessage botMessage = messageHandlingService.handleTextMessage(userTextMessage);
        SendMessage sendMessage = sendMessageService.create(
            userTextMessage.getChatId(), 
            userTextMessage.getUser().getLanguageCode(), 
            botMessage
        );

        outcomingMessageGateway.sendMessage(sendMessage);
    }
    
}
