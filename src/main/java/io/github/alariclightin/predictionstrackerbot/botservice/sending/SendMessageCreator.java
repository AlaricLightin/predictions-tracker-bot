package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.data.settings.MessageSettings;
import io.github.alariclightin.predictionstrackerbot.data.settings.MessageSettingsService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.MessageSourceArgument;

class SendMessageCreator implements Supplier<SendMessage> {
    private final MessageSource messageSource;
    private final MessageSettingsService messageSettingsService;

    private final long chatId;
    private final BotMessage botMessage;

    private MessageSettings messageSettings;

    SendMessageCreator(
        MessageSource messageSource,
        MessageSettingsService messageSettingsService,
        long chatId,
        BotMessage botMessage) {
        
        this.messageSource = messageSource;
        this.messageSettingsService = messageSettingsService;
        this.chatId = chatId;
        this.botMessage = botMessage;
    }

    @Override
    public SendMessage get() {
        messageSettings = messageSettingsService.getSettings(chatId);

        if (botMessage instanceof BotTextMessage botTextMessage) {
            return getResultForTextMessage(botTextMessage);
        }
        else
            throw new IllegalArgumentException("Unsupported message type: " + botMessage.getClass());
    }

    private SendMessage getResultForTextMessage(BotTextMessage botTextMessage) {
        StringBuilder text = new StringBuilder();
        for (BotTextMessage.TextData textData : botTextMessage.textDataList()) {
            if (text.length() > 0)
                text.append("\n\n");
            text.append(
                    messageSource.getMessage(
                            textData.messageId(),
                            convertMessageArguments(textData.args()),
                            messageSettings.locale()));
        }


        return SendMessage.builder()
            .chatId(chatId)
            .text(text.toString())
            .replyMarkup(getKeyboard(botTextMessage.keyboard()))
            .build();    
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Object[] convertMessageArguments(Object[] args) {
        Object[] result = new Object[args.length];
        for (int idx = 0; idx < result.length; idx++) {
            Object object = args[idx];
            if (object instanceof Instant instant) {
                result[idx] = LocalDateTime
                    .ofInstant(instant, messageSettings.timezone())
                    .format(DATE_TIME_FORMATTER); 
            }
            else if (object instanceof MessageSourceArgument argument) {
                result[idx] = messageSource.getMessage(
                    argument.messageId(), null, messageSettings.locale());
            }
            else
                result[idx] = object;
        }
        return result;
    }

    private InlineKeyboardMarkup getKeyboard(BotKeyboard botKeyboard) {
        if (botKeyboard == null)
            return null;

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(
            botKeyboard.buttons().stream() 
            .map(row ->
                row.stream()
                    .map(button -> {
                        String text = messageSource.getMessage(
                            button.messageId(), null, messageSettings.locale());
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

}
