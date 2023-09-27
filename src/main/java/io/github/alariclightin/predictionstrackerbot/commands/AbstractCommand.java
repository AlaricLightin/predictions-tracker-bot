package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public abstract class AbstractCommand implements CommandProcessor {
    
    protected SendMessageBuilder createResponseMessageBuilder(Message message) {
        return SendMessage.builder()
            .chatId(getUserId(message));
    }

    protected long getUserId(Message message) {
        return message.getFrom().getId();
    }

    @Override
    public BotCommand createBotCommand() {
        // TODO Fix description
        return new BotCommand(getCommandName(), "Description of " + getCommandName() + " command");
    }
}
