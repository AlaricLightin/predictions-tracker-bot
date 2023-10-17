package io.github.alariclightin.predictionstrackerbot.messages;

import java.util.List;

public record BotMessageList(
    List<BotMessage> botMessages
) implements BotMessage {
    
}
