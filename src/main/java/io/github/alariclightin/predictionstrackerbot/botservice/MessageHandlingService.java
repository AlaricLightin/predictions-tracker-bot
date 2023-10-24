package io.github.alariclightin.predictionstrackerbot.botservice;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedButtonCallbackQueryException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

public interface MessageHandlingService {
    BotMessage handleTextMessage(UserTextMessage message);

    BotMessage handleCallback(ButtonCallbackQuery userCallbackQuery) throws UnexpectedButtonCallbackQueryException;
}
