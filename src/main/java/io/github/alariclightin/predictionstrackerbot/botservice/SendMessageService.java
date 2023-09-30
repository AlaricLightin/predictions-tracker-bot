package io.github.alariclightin.predictionstrackerbot.botservice;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;

public interface SendMessageService {

    SendMessage create(long userId, String langugeCode, BotMessage botMessage);

}
