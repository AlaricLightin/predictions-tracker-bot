package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

@Service
class SendMessageServiceImpl implements SendMessageService{
    private final MessageSource messageSource;

    SendMessageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public SendMessage create(long userId, String languageCode, BotMessage botMessage) {
        if (botMessage instanceof BotMessageList botMessageList) {
            return getResultForMessageList(userId, languageCode, botMessageList);
        }
        if (botMessage instanceof BotTextMessage botTextMessage) {
            return getResultForTextMessage(userId, languageCode, botTextMessage);
        }
        else
            throw new IllegalStateException("Unsupported message type: " + botMessage.getClass());
    }

    private SendMessage getResultForMessageList(long userId, String languageCode, BotMessageList botMessageList) {
        String text = botMessageList.botMessages().stream()
            .filter(m -> m instanceof BotTextMessage)
            .map(m -> getTextString(userId, languageCode, (BotTextMessage) m))
            .collect(Collectors.joining("\n\n"));

        return getResultForText(userId, text);
    }

    private SendMessage getResultForTextMessage(long userId, String languageCode, BotTextMessage botTextMessage) {
        String text = getTextString(userId, languageCode, botTextMessage);        
        return getResultForText(userId, text);
    }

    private String getTextString(long userId, String languageCode, BotTextMessage botTextMessage) {
        return messageSource.getMessage(
            botTextMessage.messageId(), botTextMessage.args(), Locale.forLanguageTag(languageCode));
    }

    private SendMessage getResultForText(long userId, String text) {
        return SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();
    }
    
}
