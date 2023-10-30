package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

@Service
class SendMessageCreatorFactoryImpl implements SendMessageCreatorFactory {
    private final MessageSource messageSource;
    private final UserTimezoneService userTimezoneService;

    SendMessageCreatorFactoryImpl(
        MessageSource messageSource,
        UserTimezoneService userTimezoneService) {
        
        this.messageSource = messageSource;
        this.userTimezoneService = userTimezoneService;
    }

    @Override
    public Supplier<SendMessage> getCreator(long chatId, String languageCode, BotMessage botMessage) {
        return new SendMessageCreator(
            messageSource,
            userTimezoneService,
            chatId,
            languageCode,
            botMessage
        );
    }


}
