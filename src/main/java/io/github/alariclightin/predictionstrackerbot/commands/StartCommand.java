package io.github.alariclightin.predictionstrackerbot.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
class StartCommand extends AbstractCommand {

    @Override
    public SendMessage handleCommand(Message message) {
        return createResponseMessageBuilder(message)
            .text("Hello, " + message.getFrom().getFirstName() + "!")
            .build();
    }

    @Override
    public String getCommandName() {
        return "start";
    }
    
}
