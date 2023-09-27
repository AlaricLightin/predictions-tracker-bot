package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CommandsConfig {
    @Bean("commandProcessorMap")
    Map<String, CommandProcessor> commandProcessorMap(List<CommandProcessor> commandProcessors) {
        Map<String, CommandProcessor> result = new HashMap<>();
        commandProcessors.forEach(processor -> result.put(processor.getCommandName(), processor));
        return result;
    }

    @Bean("waitedResponseHandlerMap")
    Map<String, WaitedResponseHandler> waitedResponseHandlerMap(
        List<WaitedResponseHandler> waitedResponseHandlers) {
            
        Map<String, WaitedResponseHandler> result = new HashMap<>();
        waitedResponseHandlers.forEach(handler -> result.put(handler.getCommandName(), handler));
        return result;
    }
}
