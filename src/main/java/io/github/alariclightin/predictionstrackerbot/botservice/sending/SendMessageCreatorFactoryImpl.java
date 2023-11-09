package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.data.settings.MessageSettingsService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class SendMessageCreatorFactoryImpl implements SendMessageCreatorFactory {
    private final MessageSource messageSource;
    private final MessageSettingsService messageSettingsService;

    SendMessageCreatorFactoryImpl(
        MessageSource messageSource,
        MessageSettingsService messageSettingsService) {
        
        this.messageSource = messageSource;
        this.messageSettingsService = messageSettingsService;
    }

    @Override
    public Supplier<SendMessage> getCreator(long chatId, BotMessage botMessage) {
        return new SendMessageCreator(
            messageSource,
            messageSettingsService,
            chatId,
            botMessage
        );
    }


}
