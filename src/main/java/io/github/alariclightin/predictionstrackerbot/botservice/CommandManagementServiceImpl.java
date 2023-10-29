package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;

@Service
class CommandManagementServiceImpl implements CommandManagementService {
    private final Map<String, Map<String, MessageHandler>> messageHandlersMap;
    private final MessageSource messageSource;

    CommandManagementServiceImpl(
        @Qualifier("messageHandlersMap") Map<String, Map<String, MessageHandler>> messageHandlersMap,
        MessageSource messageSource
    ) {

        this.messageHandlersMap = messageHandlersMap;
        this.messageSource = messageSource;
    }

    @Override
    public List<BotCommand> getBotCommands() {
        return messageHandlersMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
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
        // TODO add internationalization
        return new BotCommand(commandName, 
            messageSource.getMessage(
                "bot.command-description." + commandName, null, Locale.forLanguageTag("en")));
    }
    
}
