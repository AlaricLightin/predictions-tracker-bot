package io.github.alariclightin.predictionstrackerbot.messages;

public record BotMessageList(
    BotMessage... botMessages
) implements BotMessage {
    
}
