package io.github.alariclightin.predictionstrackerbot.botservice;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandlingService {
    SendMessage handleMessage(Message message);
}
