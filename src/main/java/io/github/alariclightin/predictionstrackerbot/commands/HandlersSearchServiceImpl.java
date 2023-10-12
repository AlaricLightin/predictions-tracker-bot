package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class HandlersSearchServiceImpl implements HandlersSearchService {
    private final Map<String, Map<String, MessageHandler>> handlers;

    HandlersSearchServiceImpl(@Qualifier("messageHandlersMap") Map<String, Map<String, MessageHandler>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public MessageHandler getHandler(Message message, WaitedResponseState state) throws UnexpectedMessageException {
        String command;
        String phase;

        if (message.isCommand()) {
            command = message.getText().substring(1);
            phase = MessageHandler.START_PHASE;
        } else {
            if (state == null)
                throw new UnexpectedMessageException("bot.responses.error.unexpected-message");
            
            command = state.commandName();
            phase = state.phase();
        }

        Map<String, MessageHandler> commandMap = handlers.get(command);
        if (commandMap == null)
            throw new UnexpectedMessageException("bot.responses.error.unexpected-command", command);

        MessageHandler handler = commandMap.get(phase);
        if (handler != null)
            return handler;
        else
            throw new IllegalStateException("No handlers for phase " + state.phase() + " of command " + state.commandName());
    }
    
}
