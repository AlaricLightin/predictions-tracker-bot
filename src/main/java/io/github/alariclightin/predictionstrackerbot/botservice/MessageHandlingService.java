package io.github.alariclightin.predictionstrackerbot.botservice;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;

public interface MessageHandlingService {
    SendMessage handleMessage(UserTextMessage message);
}
