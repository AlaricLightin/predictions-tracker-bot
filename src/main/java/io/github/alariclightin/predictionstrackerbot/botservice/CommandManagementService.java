package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface CommandManagementService {
    List<BotCommand> getBotCommands();
}
