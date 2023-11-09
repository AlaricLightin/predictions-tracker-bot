package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.function.Supplier;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

interface SendMessageCreatorFactory {
    Supplier<SendMessage> getCreator(
        long chatId, BotMessage botMessage);
}
