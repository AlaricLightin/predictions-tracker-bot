package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

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
            throw new IllegalArgumentException("Unsupported message type: " + botMessage.getClass());
    }

    private SendMessage getResultForMessageList(long userId, String languageCode, BotMessageList botMessageList) {
        return Arrays.stream(botMessageList.botMessages())
            .map(m -> create(userId, languageCode, m))
            .collect(Collectors.collectingAndThen(Collectors.toList(), this::joinMessages));
    }

    private SendMessage getResultForTextMessage(long userId, String languageCode, BotTextMessage botTextMessage) {
        String text = messageSource.getMessage(
            botTextMessage.messageId(), botTextMessage.args(), Locale.forLanguageTag(languageCode));        
        return SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();    
    }

    private SendMessage joinMessages(List<SendMessage> messages) {
        if (messages.isEmpty())
            throw new IllegalArgumentException("Empty messages list");
        
        if (messages.size() == 1)
            return messages.get(0);
        
        StringBuilder text = new StringBuilder();
        ReplyKeyboard replyKeyboard = null;
        String chatId = messages.get(0).getChatId();

        for (SendMessage message : messages) {
            if (!message.getText().isEmpty()) {
                if (text.length() > 0)
                    text.append("\n\n");
                text.append(message.getText());
            }
            
            if (message.getReplyMarkup() != null) {
                if (replyKeyboard != null)
                    throw new IllegalArgumentException("Multiple reply keyboards");
                replyKeyboard = message.getReplyMarkup();
            }
        }

        return SendMessage.builder()
            .chatId(chatId)
            .text(text.toString())
            .replyMarkup(replyKeyboard)
            .build();
    }

    @Override
    public SendMessage create(long userId, BotMessage botMessage) {
        // TODO store language code in user settings
        return create(userId, "en", botMessage);
    }
    
}
