package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public interface HandlersSearchService {

    MessageHandler getHandler(WaitedResponseState state) throws UnexpectedUserMessageException;

}
