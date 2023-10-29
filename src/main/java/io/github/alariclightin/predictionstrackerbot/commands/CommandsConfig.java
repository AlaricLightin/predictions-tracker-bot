package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Configuration
class CommandsConfig {

    @Bean("messageHandlersMap")
    Map<String, Map<String, MessageHandler>> messageHandlersMap(
        List<MessageHandler> messageHandlersMap) {
            
        Map<String, Map<String, MessageHandler>> result = new HashMap<>();
        messageHandlersMap.forEach(handler -> {
            var commandMap = result.computeIfAbsent(handler.getCommandName(), k -> new HashMap<>());
            commandMap.put(handler.getPhaseName(), handler);
        });
        return result;
    }

    @Bean
    MessageHandler startCommand() {
        return new MessageHandlerBuilder<>()
            .setCommandName("start")
            .setResponseMessageFunc((message, data) -> 
                new BotTextMessage("bot.responses.start", message.getUser().getFirstName()))
            .build();
    }
}
