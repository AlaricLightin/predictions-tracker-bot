package io.github.alariclightin.predictionstrackerbot.bot;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;

@Service
class UpdateHandlerServiceImpl implements UpdateHandlerService {
    private final MessageHandlingService messageHandlingService;

    UpdateHandlerServiceImpl(MessageHandlingService messageHandlingService) {
        this.messageHandlingService = messageHandlingService;
    }

    @Override
    public Optional<SendMessage> handleUpdate(Update update) {
        Message incomingMessage = update.getMessage();
        if (incomingMessage == null) {
            return Optional.empty();
        }

        SendMessage message = messageHandlingService.handleMessage(new UserTextMessage(incomingMessage));
        return Optional.of(message);
    }
    
}
