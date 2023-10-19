package io.github.alariclightin.predictionstrackerbot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface BotService {
    
    void sendMessage(SendMessage sendMessage);
    
}
