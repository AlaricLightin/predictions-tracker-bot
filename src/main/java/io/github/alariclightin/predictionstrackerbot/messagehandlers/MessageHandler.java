package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public interface MessageHandler {
    public static final String START_PHASE = "start";

    ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException;
    
    String getCommandName();

    default String getPhaseName() {
        return START_PHASE;
    }
}
