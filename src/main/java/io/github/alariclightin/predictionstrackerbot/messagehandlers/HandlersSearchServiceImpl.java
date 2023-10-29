package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class HandlersSearchServiceImpl implements HandlersSearchService {
    private final Map<String, Map<String, MessageHandler>> handlers;

    HandlersSearchServiceImpl(@Qualifier("messageHandlersMap") Map<String, Map<String, MessageHandler>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public MessageHandler getHandler(WaitedResponseState state) throws UnexpectedUserMessageException {
        if (state == null)
            throw new UnexpectedUserMessageException("bot.responses.error.unexpected-message");            

        Map<String, MessageHandler> commandMap = handlers.get(state.commandName());
        if (commandMap == null)
            throw new UnexpectedUserMessageException("bot.responses.error.unexpected-command", state.commandName());

        MessageHandler handler = commandMap.get(state.phase());
        if (handler != null)
            return handler;
        else
            throw new IllegalStateException("No handlers for phase " + state.phase() + " of command " + state.commandName());
    }
    
}
