package io.github.alariclightin.predictionstrackerbot.commands;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public interface HandlersSearchService {

    MessageHandler getHandler(WaitedResponseState state) throws UnexpectedUserMessageException;

}
