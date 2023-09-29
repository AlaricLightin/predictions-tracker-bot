package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

@Service
class SendMessageServiceImpl implements SendMessageService{

    @Override
    public SendMessage create(long id, BotMessage botMessage) {
        if (botMessage instanceof BotTextMessage botTextMessage) {
            return SendMessage.builder()
                .chatId(id)
                .text(botTextMessage.text())
                .build();
        }
        else
            throw new IllegalStateException("Unsupported message type: " + botMessage.getClass());
    }
    
}
