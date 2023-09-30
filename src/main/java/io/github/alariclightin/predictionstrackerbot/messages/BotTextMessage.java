package io.github.alariclightin.predictionstrackerbot.messages;

public record BotTextMessage(
    String messageId, 
    Object... args
) implements BotMessage {
}
