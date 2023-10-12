package io.github.alariclightin.predictionstrackerbot.commands;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public record MessageHandlingResult(
    BotMessage botMessage,
    WaitedResponseState newState
) {
    
}
