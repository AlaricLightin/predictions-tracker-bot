package io.github.alariclightin.predictionstrackerbot.botservice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Service
class SendMessageService {
    private final MessageSource messageSource;
    private final UserTimezoneService userTimezoneService;

    SendMessageService(
        MessageSource messageSource,
        UserTimezoneService userTimezoneService) {
        
        this.messageSource = messageSource;
        this.userTimezoneService = userTimezoneService;
    }

    @ServiceActivator(inputChannel = "botMessageChannel", outputChannel = "outcomingMessagesChannel")
    public SendMessage create(
        @Header("chatId") long chatId, 
        // TODO store language code in user settings
        @Header(name = "languageCode", required = false, defaultValue = "en") String languageCode, 
        @Payload BotMessage botMessage) {

        return createMessage(chatId, Locale.forLanguageTag(languageCode), botMessage);
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
            botTextMessage.messageId(), 
            convertMessageArguments(userId, botTextMessage.args()), 
            locale);

        return SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();    
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Object[] convertMessageArguments(long userId, Object[] args) {
        Object[] result = new Object[args.length];
        for (int idx = 0; idx < result.length; idx++) {
            Object object = args[idx];
            if (object instanceof Instant instant) {
                result[idx] = LocalDateTime
                    .ofInstant(instant, userTimezoneService.getTimezone(userId))
                    .format(DATE_TIME_FORMATTER); 
            }
            else
                result[idx] = object;
        }
        return result;
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
                            .callbackData(button.getCallbackData())
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

    @ServiceActivator(inputChannel = "botCallbackAnswerChannel", outputChannel = "outcomingMessagesChannel")
    public AnswerCallbackQuery createAnswerCallbackQuery(
        @Header("callbackId") String callbackQueryId, 
        @Header("languageCode") String languageCode, 
        @Payload BotCallbackAnswer botCallbackAnswer) {

        String textId = botCallbackAnswer.messageId();
        String text = textId != null && !textId.isEmpty()
            ? messageSource.getMessage(textId, null, Locale.forLanguageTag(languageCode))
            : "";

        return AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQueryId)
            .text(text)
            .build();
    }
    
}
