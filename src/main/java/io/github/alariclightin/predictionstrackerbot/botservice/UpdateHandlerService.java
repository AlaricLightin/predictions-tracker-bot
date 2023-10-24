package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.stereotype.Service;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedButtonCallbackQueryException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class UpdateHandlerService {
    private final MessageHandlingService messageHandlingService;
    UpdateHandlerService(MessageHandlingService messageHandlingService) {
        this.messageHandlingService = messageHandlingService;
    }

    @ServiceActivator(inputChannel = "incomingUserMessageChannel", outputChannel = "afterHandlingChannel")
    public BotMessage handleTextMessage(UserTextMessage userTextMessage) {
        return messageHandlingService.handleTextMessage(userTextMessage);
    }

    @Splitter(inputChannel = "incomingCallbackQueryChannel", outputChannel = "afterHandlingChannel")
    public List<Object> handleCallback(ButtonCallbackQuery userCallbackQuery) {
        try {
            BotMessage botMessage = messageHandlingService.handleCallback(userCallbackQuery);
            return List.of(botMessage, new BotCallbackAnswer(""));
        } catch (UnexpectedButtonCallbackQueryException e) {
            return List.of(new BotCallbackAnswer("bot.callback.button-error"));
        }
    } 
}
