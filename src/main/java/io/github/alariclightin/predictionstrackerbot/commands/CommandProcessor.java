package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface CommandProcessor {
    SendMessage handleCommand(Message message);

    String getCommandName();

    BotCommand createBotCommand();
}
