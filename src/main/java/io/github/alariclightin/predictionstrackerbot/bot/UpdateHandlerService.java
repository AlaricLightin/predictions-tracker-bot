package io.github.alariclightin.predictionstrackerbot.bot;

import java.util.Optional;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This service is responsible for handling updates from the Telegram API.
 */
interface UpdateHandlerService {
    Optional<SendMessage> handleUpdate(Update update);
}
