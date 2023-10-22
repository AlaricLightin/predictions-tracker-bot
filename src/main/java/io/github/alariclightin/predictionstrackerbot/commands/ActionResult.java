package io.github.alariclightin.predictionstrackerbot.commands;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public record ActionResult(
    BotMessage botMessage,
    WaitedResponseState newState
) {
    
}
