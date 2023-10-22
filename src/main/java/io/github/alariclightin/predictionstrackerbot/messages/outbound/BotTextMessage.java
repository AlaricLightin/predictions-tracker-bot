package io.github.alariclightin.predictionstrackerbot.messages.outbound;

public record BotTextMessage(
    String messageId, 
    Object... args
) implements BotMessage {
}
