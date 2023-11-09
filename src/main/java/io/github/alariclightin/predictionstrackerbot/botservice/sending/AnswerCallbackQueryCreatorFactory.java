package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;

interface AnswerCallbackQueryCreatorFactory {
    Supplier<AnswerCallbackQuery> create(
        String callbackQueryId, long userId, BotCallbackAnswer botCallbackAnswer);
}
