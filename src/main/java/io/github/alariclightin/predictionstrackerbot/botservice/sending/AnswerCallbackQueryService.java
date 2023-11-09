package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;

@Service
class AnswerCallbackQueryService {
    private final AnswerCallbackQueryCreatorFactory answerCallbackQueryCreatorFactory;

    AnswerCallbackQueryService(
        AnswerCallbackQueryCreatorFactory answerCallbackQueryCreatorFactory
    ) {
        this.answerCallbackQueryCreatorFactory = answerCallbackQueryCreatorFactory;
    }

    @ServiceActivator(inputChannel = "botCallbackAnswerChannel", outputChannel = "outcomingMessagesChannel")
    public AnswerCallbackQuery createAnswerCallbackQuery(
        @Header("callbackId") String callbackQueryId, 
        @Header("chatId") long userId, 
        @Payload BotCallbackAnswer botCallbackAnswer) {

        Supplier<AnswerCallbackQuery> creator = answerCallbackQueryCreatorFactory.create(
            callbackQueryId, userId, botCallbackAnswer);
        return creator.get();
    }
}
