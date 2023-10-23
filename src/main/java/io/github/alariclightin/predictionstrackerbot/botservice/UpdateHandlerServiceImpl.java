package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class UpdateHandlerServiceImpl implements UpdateHandlerService {
    private final MessageHandlingService messageHandlingService;
    private final SendMessageService sendMessageService;

    UpdateHandlerServiceImpl(
        MessageHandlingService messageHandlingService,
        SendMessageService sendMessageService) {
            
        this.messageHandlingService = messageHandlingService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public Optional<SendMessage> handleUpdate(Update update) {
        Message incomingMessage = update.getMessage();
        if (incomingMessage == null) {
            return Optional.empty();
        }

        BotMessage botMessage = messageHandlingService.handleTextMessage(
            new UserTextMessage(incomingMessage));
        SendMessage sendMessage = sendMessageService.create(
            incomingMessage.getChatId(), 
            incomingMessage.getFrom().getLanguageCode(), 
            botMessage
        );

        return Optional.of(sendMessage);
    }
    
}
