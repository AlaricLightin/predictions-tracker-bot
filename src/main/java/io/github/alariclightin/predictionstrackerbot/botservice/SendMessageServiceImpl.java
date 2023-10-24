package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Service
class SendMessageServiceImpl implements SendMessageService{
    private final MessageSource messageSource;

    SendMessageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public SendMessage create(long userId, String languageCode, BotMessage botMessage) {
        return createMessage(userId, Locale.forLanguageTag(languageCode), botMessage);
    }

    private SendMessage createMessage(long userId, Locale locale, BotMessage botMessage) {
        if (botMessage instanceof BotMessageList botMessageList) {
            return getResultForMessageList(userId, locale, botMessageList);
        }
        else if (botMessage instanceof BotTextMessage botTextMessage) {
            return getResultForTextMessage(userId, locale, botTextMessage);
        }
        else if (botMessage instanceof BotKeyboard botKeyboard) {
            return SendMessage.builder()
                .chatId(userId)
                .text("")
                .replyMarkup(getKeyboard(userId, locale, botKeyboard))
                .build();
        }
        else
            throw new IllegalArgumentException("Unsupported message type: " + botMessage.getClass());
    }

    private SendMessage getResultForMessageList(long userId, Locale locale, BotMessageList botMessageList) {
        return botMessageList.botMessages().stream()
            .map(m -> createMessage(userId, locale, m))
            .collect(Collectors.collectingAndThen(Collectors.toList(), this::joinMessages));
    }

    private SendMessage getResultForTextMessage(long userId, Locale locale, BotTextMessage botTextMessage) {
        String text = messageSource.getMessage(
            botTextMessage.messageId(), botTextMessage.args(), locale);        
        return SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();    
    }

    private InlineKeyboardMarkup getKeyboard(long userId, Locale locale, BotKeyboard botKeyboard) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(
            botKeyboard.buttons().stream() 
            .map(row ->
                row.stream()
                    .map(button -> {
                        String text = messageSource.getMessage(
                            button.messageId(), null, locale);
                        return InlineKeyboardButton.builder()
                            .text(text)
                            .callbackData(button.callbackString())
                            .build();
                    })
                    .toList()
            )
            .toList()
        );

        return keyboardMarkup;
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

    @Override
    public AnswerCallbackQuery createAnswerCallbackQuery(
        String callbackQueryId, String languageCode, String textId) {

        String text = textId != null && !textId.isEmpty()
            ? messageSource.getMessage(textId, null, Locale.forLanguageTag(languageCode))
            : "";

        return AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQueryId)
            .text(text)
            .build();
    }
    
}
