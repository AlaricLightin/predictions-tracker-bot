package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class HandlersConfig {
    
    @Bean("messageHandlersMap")
    Map<String, Map<String, MessageHandler>> messageHandlersMap(
        List<MessageHandler> messageHandlersList,
        List<HandlersSequence> handlersSequences) {
            
        HashMap<String, Map<String, MessageHandler>> result = new HashMap<>();
        messageHandlersList.forEach(handler -> {
            var commandMap = result.computeIfAbsent(handler.getCommandName(), k -> new HashMap<>());
            commandMap.put(handler.getPhaseName(), handler);
        });

        handlersSequences.forEach(sequence -> {
            var commandMap = new HashMap<String, MessageHandler>();
            sequence.handlers().forEach(handler -> {
                commandMap.put(handler.getPhaseName(), handler);
            });
            result.put(sequence.commandName(), commandMap);
        });

        return result;
    }

}
