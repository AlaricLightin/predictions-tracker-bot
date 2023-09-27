package io.github.alariclightin.predictionstrackerbot.states;

public record WaitedResponseState(
    String commandName,
    String phase,
    Object data
) {}
