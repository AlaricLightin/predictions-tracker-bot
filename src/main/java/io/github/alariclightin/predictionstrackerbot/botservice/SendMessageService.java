package io.github.alariclightin.predictionstrackerbot.botservice;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

public interface SendMessageService {

    SendMessage create(long userId, String langugeCode, BotMessage botMessage);

    SendMessage create(long userId, BotMessage botMessage);

    AnswerCallbackQuery createAnswerCallbackQuery(
        String callbackQueryId, String languageCode, String textId);

}
