package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

@Service
class SendMessageServiceImpl implements SendMessageService{
    private final MessageSource messageSource;

    SendMessageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public SendMessage create(long userId, String languageCode, BotMessage botMessage) {
        if (botMessage instanceof BotTextMessage botTextMessage) {
            String text = messageSource.getMessage(
                botTextMessage.messageId(), botTextMessage.args(), Locale.forLanguageTag(languageCode));
            
            return SendMessage.builder()
                .chatId(userId)
                .text(text)
                .build();
        }
        else
            throw new IllegalStateException("Unsupported message type: " + botMessage.getClass());
    }
    
}
