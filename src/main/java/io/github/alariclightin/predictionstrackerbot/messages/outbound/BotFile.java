package io.github.alariclightin.predictionstrackerbot.messages.outbound;

public record BotFile(
    String filename,
    byte[] content
) implements BotMessage {
    
}
