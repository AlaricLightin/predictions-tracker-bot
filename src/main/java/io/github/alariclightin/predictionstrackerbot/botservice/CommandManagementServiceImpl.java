package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;

@Service
class CommandManagementServiceImpl implements CommandManagementService {
    Map<String, Map<String, MessageHandler>> messageHandlersMap;

    CommandManagementServiceImpl(
        @Qualifier("messageHandlersMap") Map<String, Map<String, MessageHandler>> messageHandlersMap) {

        this.messageHandlersMap = messageHandlersMap;
    }

    @Override
    public List<BotCommand> getBotCommands() {
        return messageHandlersMap.entrySet().stream()
            .map(entry -> {
                String commandName = entry.getKey();
                Map<String, MessageHandler> commandMap = entry.getValue();
                MessageHandler startHandler = commandMap.get(MessageHandler.START_PHASE);
                if (startHandler == null)
                    throw new IllegalStateException("No start handler for command " + commandName);
                
                return createBotCommand(commandName);
            })
            .toList();
    }

    private BotCommand createBotCommand(String commandName) {
        // TODO Fix description
        return new BotCommand(commandName, "Description of " + commandName + " command");
    }
    
}
