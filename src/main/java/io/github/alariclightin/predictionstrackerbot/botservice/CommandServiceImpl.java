package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.commands.CommandProcessor;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;

@Service
class CommandServiceImpl implements CommandHandlingService, CommandManagementService {
    private final Map<String, CommandProcessor> commandProcessorMap;
    private final StateHolderService stateHolderService;

    CommandServiceImpl(
        @Qualifier("commandProcessorMap") Map<String, CommandProcessor> commandProcessorMap,
        StateHolderService stateHolderService) {

        this.commandProcessorMap = commandProcessorMap;
        this.stateHolderService = stateHolderService;
    }

    @Override
    public BotMessage handle(Message message) {
        String command = message.getText().split(" ")[0].substring(1);
        if (command.isBlank())
            throw new IllegalArgumentException("Command is blank");

        stateHolderService.deleteState(message.getFrom().getId());
        CommandProcessor processor = commandProcessorMap.get(command);
        if (processor != null)
            return processor.handleCommand(message);
        else
            return new BotTextMessage("bot.responses.error.unexpected-command", command);
    }

    @Override
    public List<BotCommand> getBotCommands() {
        return commandProcessorMap.values().stream()
            .map(CommandProcessor::createBotCommand)
            .toList();
    }
    
}
