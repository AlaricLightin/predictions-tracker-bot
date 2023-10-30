package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

class SendMessageCreator implements Supplier<SendMessage> {
    private final MessageSource messageSource;
    private final UserTimezoneService userTimezoneService;

    private final long chatId;
    private final Locale locale;
    private final BotMessage botMessage;

    SendMessageCreator(
        MessageSource messageSource,
        UserTimezoneService userTimezoneService,
        long chatId,
        String languageCode,
        BotMessage botMessage) {
        
        this.messageSource = messageSource;
        this.userTimezoneService = userTimezoneService;
        this.chatId = chatId;
        this.locale = Locale.forLanguageTag(languageCode);
        this.botMessage = botMessage;
    }

    @Override
    public SendMessage get() {
        return createMessage(botMessage); 
    }

    private SendMessage createMessage(BotMessage botMessage) {
        if (botMessage instanceof BotMessageList botMessageList) {
            return getResultForMessageList(botMessageList);
        }
        else if (botMessage instanceof BotTextMessage botTextMessage) {
            return getResultForTextMessage(botTextMessage);
        }
        else if (botMessage instanceof BotKeyboard botKeyboard) {
            return SendMessage.builder()
                .chatId(chatId)
                .text("")
                .replyMarkup(getKeyboard(botKeyboard))
                .build();
        }
        else
            throw new IllegalArgumentException("Unsupported message type: " + botMessage.getClass());
    }
    
    private SendMessage getResultForMessageList(BotMessageList botMessageList) {
        return botMessageList.botMessages().stream()
            .map(m -> createMessage(m))
            .collect(Collectors.collectingAndThen(Collectors.toList(), this::joinMessages));
    }

    private SendMessage getResultForTextMessage(BotTextMessage botTextMessage) {
        String text = messageSource.getMessage(
            botTextMessage.messageId(), 
            convertMessageArguments(botTextMessage.args()), 
            locale);

        return SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .build();    
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Object[] convertMessageArguments(Object[] args) {
        ZoneId zoneId = userTimezoneService.getTimezone(chatId);
        Object[] result = new Object[args.length];
        for (int idx = 0; idx < result.length; idx++) {
            Object object = args[idx];
            if (object instanceof Instant instant) {
                result[idx] = LocalDateTime
                    .ofInstant(instant, zoneId)
                    .format(DATE_TIME_FORMATTER); 
            }
            else
                result[idx] = object;
        }
        return result;
    }

    private InlineKeyboardMarkup getKeyboard(BotKeyboard botKeyboard) {
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

}
