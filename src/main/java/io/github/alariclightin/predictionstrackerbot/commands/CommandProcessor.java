package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;

public interface CommandProcessor {
    BotMessage handleCommand(Message message);

    String getCommandName();

    BotCommand createBotCommand();
}
