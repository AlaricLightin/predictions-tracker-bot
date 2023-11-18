package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import java.util.List;

public record HandlersSequence(
    String commandName,
    List<MessageHandler> handlers
) {
    
}
