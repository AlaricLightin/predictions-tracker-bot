package io.github.alariclightin.predictionstrackerbot.states;

public interface StateHolderService {
    void saveState(long userId, WaitedResponseState state);
    // TODO add separate method getCommand?
    WaitedResponseState getState(long userId);
    void deleteState(long userId);
}
