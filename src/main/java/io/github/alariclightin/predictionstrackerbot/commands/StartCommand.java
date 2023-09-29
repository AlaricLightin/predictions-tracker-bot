package io.github.alariclightin.predictionstrackerbot.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

@Service
class StartCommand extends AbstractCommand {

    @Override
    public BotMessage handleCommand(Message message) {
        return new BotTextMessage("Hello, " + message.getFrom().getFirstName() + "!");
    }

    @Override
    public String getCommandName() {
        return "start";
    }
    
}
