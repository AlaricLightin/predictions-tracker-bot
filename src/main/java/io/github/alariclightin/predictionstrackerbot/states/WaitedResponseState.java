package io.github.alariclightin.predictionstrackerbot.states;

public record WaitedResponseState(
    String commandName,
    String phase,
    Object data
) {

    public WaitedResponseState(String commandName, String phase) {
        this(commandName, phase, null);
    }
}
