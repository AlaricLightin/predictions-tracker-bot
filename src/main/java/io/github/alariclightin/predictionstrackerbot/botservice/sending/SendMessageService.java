package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class SendMessageService {
    private final SendMessageCreatorFactory sendMessageCreatorFactory;

    SendMessageService(SendMessageCreatorFactory sendMessageCreatorFactory) {
        this.sendMessageCreatorFactory = sendMessageCreatorFactory;
    }

    @ServiceActivator(inputChannel = "botMessageChannel", outputChannel = "outcomingMessagesChannel")
    public SendMessage create(
        @Header("chatId") long chatId, 
        @Payload BotMessage botMessage) {

        Supplier<SendMessage> creator = sendMessageCreatorFactory.getCreator(
            chatId, botMessage);
        return creator.get();
    }

}
