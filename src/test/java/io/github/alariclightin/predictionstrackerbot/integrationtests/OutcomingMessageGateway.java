package io.github.alariclightin.predictionstrackerbot.integrationtests;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface OutcomingMessageGateway {
        
    void sendMessage(SendMessage sendMessage);
    void sendAnswerCallback(AnswerCallbackQuery answerCallbackQuery);
}
